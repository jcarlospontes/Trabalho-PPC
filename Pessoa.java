package src;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Pessoa extends Thread{

    private int genero; //0 = homem, 1 = mulher e 2 = outro

    private Banheiro banheiro; //regiao compartilhada do banheiro

    private int id; // identificador da pessoa.

    private String msgid; // string ao citar a pessoa com o identificador.

    private boolean bloqueado; // indica se a pessoa esta bloqueada para entrar no banheiro.

    private boolean forabanheiro; // indica se a pessoa esta fora do banheiro.

    private boolean foradobox; // indica se a pessoa esta fora do box.

    private LocalDateTime chegadafilaespera; // indica o tempo do momento em que a pessoa chega na fila de espera

    private LocalDateTime saidafilaespera; // indica o tempo do momento em que a pessoa sai da fila de espera

    private Long espera; // tempo de espera que a pessoa deve ter para chegar no banheiro.


    @Override
    public void run(){
                // faz a thread esperar entre 2 e 6 segundos simulando a chegada da pessoa ate a fila de espera.
                    try {
                        sleep(espera);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                System.out.println(getTime(banheiro.getInicialTime())+": "+msgid + "Acabou de chegar no banheiro depois de "+espera/1000+" segundos andando");
                banheiro.addLog(getTime(banheiro.getInicialTime())+": "+msgid + "Acabou de chegar no banheiro depois de "+espera/1000+" segundos andando");

                // usa a funcao de entrar no banheiro.
                synchronized(banheiro){
                    this.chegadafilaespera = LocalDateTime.now();
                    banheiro.enterBathroom(this);
                }

                // caso a pessoa ainda esteja bloqueada na fila de espera ela espera e tenta novamente entrar no banheiro.
                synchronized(banheiro){
                    while(this.forabanheiro){
                        try {
                            banheiro.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        banheiro.tentaEntrarBanheiro(this);
                    }
                }

                System.out.println(getTime(banheiro.getInicialTime())+": "+msgid + "Acabou de entrar no banheiro");
                banheiro.addLog(getTime(banheiro.getInicialTime())+": "+msgid + "Acabou de entrar no banheiro");

                synchronized(banheiro){
                    // adiciona o tempo de espera da pessoa nas listas de tempo de espera
                    this.saidafilaespera = LocalDateTime.now();
                    switch(this.genero){
                        case 0:
                            banheiro.addTempoEsperaM(getTempoEspera());
                            break;
                        case 1:
                            banheiro.addTempoEsperaF(getTempoEspera());
                            break;
                        case 2:
                            banheiro.addTempoEsperaQ(getTempoEspera());
                            break;
                    }
                    banheiro.somaquantpessoa();
                    banheiro.notifyAll();
                }

                synchronized(banheiro){
                    try {
                        banheiro.getStall(this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // usa a funcao de consumir o box
                synchronized(banheiro){
                    while(this.foradobox){
                        try {
                            banheiro.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            banheiro.tentaEntrarBox(this);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                synchronized(banheiro){
                    banheiro.removefilaBanheiro();
                    banheiro.removenupessoaban();
                }

                synchronized(banheiro){
                    banheiro.notifyAll();
                }

                System.out.println(getTime(banheiro.getInicialTime())+": "+msgid + "Acabou de entrar no box");
                banheiro.addLog(getTime(banheiro.getInicialTime())+": "+msgid + "Acabou de entrar no box");

                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                banheiro.releaseStall(this);

                System.out.println(getTime(banheiro.getInicialTime())+": "+msgid + "Acabou de sair do banheiro");
                banheiro.addLog(getTime(banheiro.getInicialTime())+": "+msgid + "Acabou de sair do banheiro");

                synchronized(banheiro){
                    if((banheiro.getTotalPessoas() == banheiro.getquantpessoa()) && (banheiro.getnuboxocupado() == 0) && (banheiro.getnupessoasBanheiro() == 0)){
                        try {
                            banheiro.geraRelatorio();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    banheiro.notifyAll();
                }

        }



    public Pessoa(Long espera,Banheiro banheiro,int genero, int id){
        this.espera = espera;
        this.foradobox = true;
        this.forabanheiro = true;
        this.bloqueado = true;
        this.banheiro = banheiro;
        this.genero = genero;
        this.id = id;
        if(genero == 0){
            System.out.println(getTime(banheiro.getInicialTime())+": "+"Pessoa "+id+" do genero masculino criado");
            banheiro.addLog(getTime(banheiro.getInicialTime())+": "+"Pessoa "+id+" do genero masculino criado");
            msgid = "Pessoa "+id+" do genero masculino: ";
        }
        else if(genero == 1){
            System.out.println(getTime(banheiro.getInicialTime())+": "+"Pessoa "+id+" do genero feminino criado");
            banheiro.addLog(getTime(banheiro.getInicialTime())+": "+"Pessoa "+id+" do genero feminino criado");
            msgid = "Pessoa "+id+" do genero feminino: ";
        }
        else{
            System.out.println(getTime(banheiro.getInicialTime())+": "+"Pessoa "+id+" do genero outro criado");
            banheiro.addLog(getTime(banheiro.getInicialTime())+": "+"Pessoa "+id+" do genero outro criado");
            msgid = "Pessoa "+id+" do genero outro: ";
        }
    }


    public int getGenero(){
        return this.genero;
    }

    public String getMsg(){
        return this.msgid;
    }

    public int getIdent(){
        return this.id;
    }
    public boolean getBloqueado(){
        return this.bloqueado;
    }
    public void setDesbloqueado(){
        this.bloqueado = false;
    }

    public boolean getnoBanheiro(){
        return this.bloqueado;
    }
    public void setNoBanheiro(){
        this.forabanheiro = false;
    }

    public boolean getNoBox(){
        return this.foradobox;
    }
    public void setNoBox(){
        this.foradobox = false;
    }

    public String getTime(){
        LocalDateTime myDateObj = LocalDateTime.now();

        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HH:mm:ss");

    
        String formattedDate = myDateObj.format(myFormatObj);

        return formattedDate;
    }

    public String getTime(LocalDateTime inicial){
        LocalDateTime myDateObj = LocalDateTime.now();

        Duration duracao = Duration.between(inicial, myDateObj);
    
        //String formattedDate = myDateObj.format(myFormatObj);

        //String formattedDate = duracao.toMinutes()+":"+duracao.toSecondsPart();
        String  formattedDate = String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duracao.toMillis()) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(duracao.toMillis())
            ),
            TimeUnit.MILLISECONDS.toSeconds(duracao.toMillis()) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(duracao.toMillis())
            )
        );

        return formattedDate;
    }

    public long getTempoEspera(){
        Duration duracaoespera = Duration.between(chegadafilaespera, saidafilaespera);

        return duracaoespera.toMillis();
    }

    public Long getEspera(){
        return this.espera;
    }

}
