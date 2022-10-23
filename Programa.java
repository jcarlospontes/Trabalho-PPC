package src;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Programa {

    public static void main(String args[]){

        int b, p; // variveis de input, b = numero de box do banheiro, p = numero de pessoas que utilizarao o banheiro.

        String box = JOptionPane.showInputDialog(null, "Digite a quantidade de box");
        String pessoaM = JOptionPane.showInputDialog(null, "Digite a quantidade de pessoas do genero masculino");
        String pessoaF = JOptionPane.showInputDialog(null, "Digite a quantidade de pessoas do genero feminino");
        String pessoaQ = JOptionPane.showInputDialog(null, "Digite a quantidade de pessoas do genero outro");
        b = Integer.parseInt(box);

        boolean primeiro = true;

        Random rand = new Random(); //variavel para conseguir valores randomicos.

        Pessoa[] pessoas; // lista onde as pessoas(threads) serao armazenadas.
        //atribui o valor do numero de pessoas dividido por 3 para mulher, homem e outros.
        int nM = Integer.parseInt(pessoaM);
        int nF = Integer.parseInt(pessoaF);
        int nQ = Integer.parseInt(pessoaQ);

        p = nM+nF+nQ;

        Banheiro banheiro = new Banheiro(b,p);

        pessoas = new Pessoa[p]; // lista onde as pessoas(threads) serao armazenadas.
        int contador = 0; // contador utilizado para listar as pessoas

        //cria as threads(cada pessoa de forma aleatoria)
        while(nM != 0 || nF != 0 || nQ != 0){
            int tipo = rand.nextInt(3);
            Long espera = rand.nextLong(2000,6000);
            if(primeiro){
                primeiro = false;
            }
            else{
                espera = espera+pessoas[contador-1].getEspera();
            }
            switch (tipo){
                case 0:
                    if(nM != 0){
                        pessoas[contador] = new Pessoa(espera, banheiro, tipo, contador);
                        contador++;
                        nM -= 1;
                    }
                    break;
                case 1:
                if(nF != 0){
                    pessoas[contador] = new Pessoa(espera, banheiro, tipo, contador);
                    contador++;
                    nF -= 1;
                    }
                    break;
                case 2:
                if(nQ != 0){
                    pessoas[contador] = new Pessoa(espera, banheiro, tipo, contador);
                    contador++;
                    nQ -= 1;
                    }
                    break;
            }
        }

        // inicia todas as threads de pessoas
        for(int x = 0; x<p ; x++){
             pessoas[x].start();
        }


        JFrame janelaprincipal = new JFrame();
        janelaprincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelaprincipal.setResizable(false);
        janelaprincipal.setTitle("O Problema do Banheiro Unissex");

        GamePanel gamePanel = new GamePanel(banheiro);
        janelaprincipal.add(gamePanel);

        janelaprincipal.pack();

        janelaprincipal.setLocationRelativeTo(null);
        janelaprincipal.setVisible(true);

        gamePanel.startGameThread();

    }
    
}
