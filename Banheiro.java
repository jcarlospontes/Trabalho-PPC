package src;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

//essa classe deve ser a sincronizacao das pessoas que entram no banheiro/esperam em filas fora do banheiro
public class Banheiro {
    
    private int nubox; //representa o valor da quantidade de box do banheiro.
    private int nuboxtotal; //representa o total de box do banheiro
    private int estadoBanheiro; // o banheiro se comporta diferente com 3 estados, 0 = qualquer genero pode entrar, 1 = livre para o mesmo genero e 2 = bloqueado.
    private Queue<Pessoa> filaBanheiro; // fila dentro do banheiro para usar o box.
    private Queue<Pessoa> filaEspera; // fila de fora do banheiro
    private int tipoBanheiro; // variavel que muda dependendo do genero utilizando o banheiro, 0 = homem, 1 = mulher, 2 = outro e 3 = vazio;
    private int totalpessoas; // indica o total de pessoas da simulacao.
    private int quantpessoas; // mostra o numero de pessoas que entram dentro do banheiro.
    private int nuboxocupado; // mostra o numero de box ocupadas.
    private int nupessoaban; // mostra o numero de pessoas que estao dentro do banheiro.
    private LocalDateTime inicial; // recebe o tempo em que a primeira pessoa entra no banheiro.
    private ArrayList<Long> esperaM = new ArrayList<Long>(); // armazena os tempos de espera de cada pessoa do sexo masculino
    private ArrayList<Long> esperaF = new ArrayList<Long>(); // armazena os tempos de espera de cada pessoa do sexo feminino
    private ArrayList<Long> esperaQ = new ArrayList<Long>(); // armazena os tempos de espera de cada pessoa do sexo outro
    private ArrayList<Long> tempousoBox = new ArrayList<Long>(); // armazena os tempos de uso de box
    private String log; // string que vai armazenar o conteudo do log gerado no final da simulacao.
    private LocalDateTime inicioOC; // indica o tempo de inicio do uso do primeiro box
    private LocalDateTime fimOC; // indica o tempo de fim do uso do ultimo box

    public Banheiro(int numbox, int numpessoa){
        this.log = "";
        this.estadoBanheiro = 0;
        filaBanheiro = new LinkedList<>();
        filaEspera = new LinkedList<>();
        nubox = numbox;
        nuboxtotal = numbox;
        this.tipoBanheiro = 3;
        this.totalpessoas = numpessoa;
        this.quantpessoas = 0;
        nuboxocupado = 0;
        nupessoaban = 0;
        inicial = LocalDateTime.now();
    }
    // faz a pessoa entrar no banheiro
    public synchronized void enterBathroom(Pessoa pessoa){
        switch (estadoBanheiro){
            // caso o banheiro e a fila de espera estejam vazios
            case 0:
                inicioOC = LocalDateTime.now();
                System.out.println(getTime()+" pegou inicio oc");
                pessoa.setNoBanheiro();
                filaBanheiro.add(pessoa);
                nupessoaban++;
                estadoBanheiro = 1;
                tipoBanheiro = pessoa.getGenero();
                break;
            // caso a fila de espera nao esteja bloqueada ainda
            case 1:
                //caso o tipo da pessoa seja diferente entao muda o estado do banheiro bloqueando para todos que chegarem
                if(tipoBanheiro != pessoa.getGenero()){
                    estadoBanheiro = 2;
                    filaEspera.add(pessoa);
                }
                else{
                    filaBanheiro.add(pessoa);
                    nupessoaban++;
                    pessoa.setNoBanheiro();
                }
                break;
            // caso a fila de espera esteja bloqueada
            case 2:
                filaEspera.add(pessoa);
                break;
        }
    }

    // funcao que faz a pessoa tentar entrar novamente no banheiro.
    public synchronized void tentaEntrarBanheiro(Pessoa pessoa){
        if(!pessoa.getBloqueado() && filaEspera.peek().equals(pessoa)){
            synchronized(this){
                if(isbanheiroVazio()){
                    inicioOC = LocalDateTime.now();
                    System.out.println(getTime()+" pegou inicio oc");
                }
                pessoa.setNoBanheiro();
                filaBanheiro.add(pessoa);
                nupessoaban++;
                filaEspera.remove();
                tipoBanheiro = pessoa.getGenero();
            }

            if(!filaEspera.isEmpty()){
                // caso a pessoa anterior seja do mesmo genero ela é desbloqueada.
                if(filaEspera.peek().getGenero() == this.tipoBanheiro){
                    synchronized(this){
                        filaEspera.peek().setDesbloqueado();
                    }
                }
            }
            else{
                estadoBanheiro = 1;
            }
        }
    }

    public synchronized void getStall(Pessoa pessoa) throws InterruptedException{ // faz pessoa consumir o box
        synchronized(this){
            if((nubox > 0) && filaBanheiro.peek().equals(pessoa)){
                nubox-=1;
                this.nuboxocupado+=1;
                pessoa.setNoBox();
            }
        }
    }

    public synchronized void tentaEntrarBox(Pessoa pessoa)throws InterruptedException{
        synchronized(this){
            if((this.nubox > 0) && (filaBanheiro.peek().equals(pessoa))){
                nubox--;
                this.nuboxocupado+=1;
                pessoa.setNoBox();
            }
        }
    }

    // libera 1 box e libera a primeira pessoa na fila de espera caso o banheiro e box estejam vazios.
    public synchronized void releaseStall(Pessoa pessoa){
        this.nubox +=1;
        this.nuboxocupado-=1;
        if(isbanheiroVazio()){
            if(filaEspera.isEmpty()){
                estadoBanheiro = 0;
                this.tipoBanheiro = 3;
            }
            else{
                filaEspera.peek().setDesbloqueado();
            }
            // pega o tempo de fim de consumo do box
            if(this.nuboxocupado == 0 && filaBanheiro.isEmpty()){
                fimOC = LocalDateTime.now();
                System.out.println(getTime()+" pegou fim oc");
                addTempoOCBox();
            }
        }
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

    public String taxaOcupacaoBox(){

        Long soma = tempousoBox.stream().reduce(0L, Long::sum);

        LocalDateTime myDateObj = LocalDateTime.now();

        Duration duracao = Duration.between(this.inicial, myDateObj);

    
        return String.format("%.2f",soma/(float)duracao.toSeconds()*100);
    }

    public int getquantpessoa(){
        return this.quantpessoas;
    }

    public void somaquantpessoa(){
        this.quantpessoas++;
    }

    public boolean isbanheiroVazio(){
        return ((filaBanheiro.isEmpty()) && (nubox == nuboxtotal));
    }

    public void setEstadoBanheiro(int estado){
        this.estadoBanheiro = estado;
    }

    public void printFilaBanheiro(){
        ArrayList<Integer> idents = new ArrayList<Integer>();
        for(Pessoa s : filaBanheiro){
            idents.add(s.getIdent());
        }
        System.out.println("Fila banheiro: "+idents.toString());
    }
    public void printFilaEspera(){
        ArrayList<Integer> idents = new ArrayList<Integer>();
        for(Pessoa s : filaEspera){
            idents.add(s.getIdent());
        }
        System.out.println("Fila espera: "+idents.toString());
    }

    public Queue<Pessoa> getfilaEspera(){
        return this.filaEspera;
    }

    public Queue<Pessoa> getfilaBanheiro(){
        return this.filaBanheiro;
    }

    public int getTipoBanheiro(){
        return this.tipoBanheiro;
    }

    public int getnubox(){
        return this.nubox;
    }
    public int getnuboxocupado(){
        return this.nuboxocupado;
    }

    public void removefilaBanheiro(){
        this.filaBanheiro.remove();
    }

    public int getnupessoasBanheiro(){
        return this.nupessoaban;
    }

    public void removenupessoaban(){
        this.nupessoaban--;
    }

    public LocalDateTime getInicialTime(){
        return this.inicial;
    }

    public int getTotalPessoas(){
        return this.totalpessoas;
    }

    public void addTempoEsperaM(long tempo){
        
        this.esperaM.add(tempo);
    }
    public void addTempoEsperaF(long tempo){
        this.esperaF.add(tempo);
    }
    public void addTempoEsperaQ(long tempo){
        this.esperaQ.add(tempo);
    }

    public void addTempoOCBox(){
        Duration duracao = Duration.between(inicioOC, fimOC);
        tempousoBox.add(duracao.toSeconds());
    }

    public void geraRelatorio() throws FileNotFoundException{
        this.log += "\n\n\n"+
            "Tempo total da simulacao: "+getTime(inicial)+
            "\nTempo total de uso do Box: "+tempousoBox.stream().reduce(0L, Long::sum)+" segundos"+
            "\n\nNumero total de pessoas:\nDo genero masculino: "+this.esperaM.size()+"\nDo genero Feminino: "+this.esperaF.size()+"\nDo genero Outro: "+this.esperaQ.size()+
            "\n\nO tempo médio de espera de pessoas: \nDo genero Masculino: "+mediaTempoEspera(esperaM)+" segundos\nDo genero Feminino: "+mediaTempoEspera(esperaF)+" segundos\nDo genero Outro: "+mediaTempoEspera(esperaQ)+" segundos"+
            "\n\nTaxa de ocupacao do box: "+taxaOcupacaoBox()+"%";
            PrintWriter out = new PrintWriter("output.txt");
            System.out.println(tempousoBox);
            out.flush();
            out.println(log);
            out.close();
    }

    public void addLog(String mensagem){
        this.log += "\n"+mensagem;
    }

    public int mediaTempoEspera(ArrayList<Long> lista){
        Long soma = lista.stream().reduce(0L, Long::sum);
        float media = (soma/1000)/lista.size();
        return Math.round(media);
    }

}
