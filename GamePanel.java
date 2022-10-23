package src;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GamePanel extends JPanel implements Runnable{
    
    boolean mapeado, despausado, gameState = false;

    Banheiro banheiro;

    JTextField texto = new JTextField();

    Random rand = new Random();

    final int originalTiteSize = 10; // 12
    final int scale = 2; // 3
    final int tileSize = originalTiteSize * scale;
    final int maxScreenCol = 30; //21
    final int maxScreenRow = 30; //21
    final int screenWidth = maxScreenCol * tileSize+235; //300
    final int screenHeight = maxScreenRow * tileSize;

    Bloco[][] matrizJogo = new Bloco[maxScreenRow][maxScreenCol]; //10x10
    Bloco[][] matrizBanheiro = new Bloco[10][10];

    String strtempo = "";

    Thread gameThread;

    //imagens
    Image imagecasan = new ImageIcon("images/casanormal.png").getImage();
    Image imagecasab = new ImageIcon("images/casablue.png").getImage();
    Image imagecasar = new ImageIcon("images/casared.png").getImage();
    Image imagecasag = new ImageIcon("images/casagray.png").getImage();
    Image imagemasc = new ImageIcon("images/masculino.png").getImage();
    Image imagefem = new ImageIcon("images/feminino.png").getImage();
    Image imageoutro = new ImageIcon("images/outros.png").getImage();
    Image imagetextfban = new ImageIcon("images/textfiladobanheiro.png").getImage();
    Image imagetextocup = new ImageIcon("images/textocupado.png").getImage();
    Image imagetextempo = new ImageIcon("images/textempo.png").getImage();

    public void setGameState(Boolean state){
        this.gameState = state;
    }
    public boolean getGameState(){
        return this.gameState;
    }

    public void mapeiaarray(){

        limpaarray();

        ArrayList<Integer> genesp = new ArrayList<Integer>();
        for(Pessoa s : banheiro.getfilaEspera()){
            genesp.add(s.getGenero());
        }

        ArrayList<Integer> genban = new ArrayList<Integer>();
        for(Pessoa s : banheiro.getfilaBanheiro()){
            genban.add(s.getGenero());
        }

        // mapeia fila de espera
        if(!genesp.isEmpty()){
            int inicial[] = {0,29};
            matrizJogo[inicial[1]][inicial[0]].setFlag(genesp.get(0));
            inicial = moveponteiro(inicial, 2);
        }
        if(!genesp.isEmpty()){
        int inicial[] = {0,28};
        int contador = 1;
        while(contador != genesp.size()){
            for(int x = 28; x>0 ; x--){
                matrizJogo[inicial[1]][inicial[0]].setFlag(genesp.get(contador));
                inicial = moveponteiro(inicial, 2);
                contador ++;
                if(contador == genesp.size()){
                    break;
                }
            }
            if(contador == genesp.size()){
                break;
            }
            matrizJogo[inicial[1]][inicial[0]].setFlag(genesp.get(contador));
            inicial = moveponteiro(inicial, 1);
            contador ++;
            if(contador == genesp.size()){
                break;
            }
            matrizJogo[inicial[1]][inicial[0]].setFlag(genesp.get(contador));
            inicial = moveponteiro(inicial, 1);
            contador ++;
            if(contador == genesp.size()){
                break;
            }
            for(int x = 0; x<28 ; x++){
                matrizJogo[inicial[1]][inicial[0]].setFlag(genesp.get(contador));
                inicial = moveponteiro(inicial, 3);
                contador ++;
                if(contador == genesp.size()){
                    break;
                }
            }
            if(contador == genesp.size()){
                break;
            }
            matrizJogo[inicial[1]][inicial[0]].setFlag(genesp.get(contador));
            inicial = moveponteiro(inicial, 1);
            contador ++;
            if(contador == genesp.size()){
                break;
            }
            matrizJogo[inicial[1]][inicial[0]].setFlag(genesp.get(contador));
            inicial = moveponteiro(inicial, 1);
            contador ++;
        }
    }



    // mapeia fila do banheiro
    if(!genban.isEmpty()){
    int inicial[] = {0,9};
    int contador = 0;
    while(contador != genban.size()){
        for(int x = 9; x>0 ; x--){
            matrizBanheiro[inicial[1]][inicial[0]].setFlag(genban.get(contador));
            inicial = moveponteiro(inicial, 2);
            contador ++;
            if(contador == genban.size()){
                break;
            }
        }
        if(contador == genban.size()){
            break;
        }
        matrizBanheiro[inicial[1]][inicial[0]].setFlag(genban.get(contador));
        inicial = moveponteiro(inicial, 1);
        contador ++;
        if(contador == genban.size()){
            break;
        }
        for(int x = 0; x<9 ; x++){
            matrizBanheiro[inicial[1]][inicial[0]].setFlag(genban.get(contador));
            inicial = moveponteiro(inicial, 3);
            contador ++;
            if(contador == genban.size()){
                break;
            }
        }
        if(contador == genban.size()){
            break;
        }
        matrizBanheiro[inicial[1]][inicial[0]].setFlag(genban.get(contador));
        inicial = moveponteiro(inicial, 1);
        contador ++;
    }
}


    }

    public int[] moveponteiro(int[] atual, int direcao){
        int posicao[] = atual;
        switch(direcao){
            case 0://cima
                posicao[0] = posicao[0] - 1;
                return posicao;

            case 1://baixo
                posicao[0] = posicao[0] + 1;
                return posicao;

            case 2://esquerda
                posicao[1] = posicao[1] - 1;
                return posicao;

            default://direita
                posicao[1] = posicao[1] + 1;
                return posicao;
        }
    }

    public void limpaarray(){
        for(int x = 0; x< 30; x++){
            for(int y = 0; y<30; y++){
                matrizJogo[x][y].setFlag(3);
            }
        }

        for(int x = 0; x< 10; x++){
            for(int y = 0; y<10; y++){
                matrizBanheiro[x][y].setFlag(3);
            }
        }
    }


    public GamePanel(Banheiro banheiro){

        this.banheiro = banheiro;
        this.setPreferredSize(new Dimension(screenWidth+((maxScreenCol+1)*2), screenHeight+((maxScreenRow+1)*2)));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run(){
        while(gameThread != null){
            if((banheiro.getquantpessoa() != banheiro.getnupessoasBanheiro()) && (banheiro.getnuboxocupado() != 0)){
                strtempo = banheiro.getTime(banheiro.getInicialTime());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            repaint();
        }
    }





    public void paintComponent(Graphics g){
    

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        g2.setColor(Color.white);

        // desenha quadrado branco
        g2.fillRect(670, 5, 219, 425); // 665, 5, 290, 650

        // desenha o texto de box
        g2.drawImage(imagetextocup, 708, 265, this);


        // desenha o tempo atual
        g2.setColor(Color.black);
        g2.drawString(strtempo, 828, 27);
        // desenha a quantidade de pessoas que entraram no banheiro.
        g2.drawString(banheiro.getquantpessoa()+"", 700, 27);

        // desenha o texto de fila do banheiro
        g2.drawImage(imagetextfban, 675, 400, this);

        // desenha o texto de tempo
        g2.drawImage(imagetextempo, 750, 7, this);

        g2.setColor(Color.white);

        // desenha casa e box
        int boxhstart = 690;
        int boxvstart = 300;
        int sizebox = 30;
        switch(banheiro.getTipoBanheiro()){
            case 0:
                g2.drawImage(imagemasc, 680, 50, this);
                g2.drawImage(imagecasab, 680, 100, this);
                g2.setColor(Color.blue);
                if(banheiro.getnuboxocupado() == 1){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                }
                else if(banheiro.getnuboxocupado() == 2){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5), boxvstart, sizebox, sizebox);
                }
                else if(banheiro.getnuboxocupado() == 3){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5), boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*2, boxvstart, sizebox, sizebox);
                }
                else if(banheiro.getnuboxocupado() == 4){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5), boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*2, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*3, boxvstart, sizebox, sizebox);
                }
                else if(banheiro.getnuboxocupado() == 5){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5), boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*2, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*3, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*4, boxvstart, sizebox, sizebox);
                }
                break;
            case 1:
                g2.drawImage(imagefem, 680, 50, this);
                g2.drawImage(imagecasar, 680, 100, this);
                g2.setColor(Color.red);
                if(banheiro.getnuboxocupado() == 1){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                }
                else if(banheiro.getnuboxocupado() == 2){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5), boxvstart, sizebox, sizebox);
                }
                else if(banheiro.getnuboxocupado() == 3){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5), boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*2, boxvstart, sizebox, sizebox);
                }
                else if(banheiro.getnuboxocupado() == 4){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5), boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*2, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*3, boxvstart, sizebox, sizebox);
                }
                else if(banheiro.getnuboxocupado() == 5){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5), boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*2, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*3, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*4, boxvstart, sizebox, sizebox);
                }
                break;
            case 2:
                g2.drawImage(imageoutro, 680, 50, this);
                g2.drawImage(imagecasag, 680, 100, this);
                g2.setColor(Color.GRAY);
                if(banheiro.getnuboxocupado() == 1){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                }
                else if(banheiro.getnuboxocupado() == 2){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5), boxvstart, sizebox, sizebox);
                }
                else if(banheiro.getnuboxocupado() == 3){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5), boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*2, boxvstart, sizebox, sizebox);
                }
                else if(banheiro.getnuboxocupado() == 4){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5), boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*2, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*3, boxvstart, sizebox, sizebox);
                }
                else if(banheiro.getnuboxocupado() == 5){
                    g2.fillRect(boxhstart, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5), boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*2, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*3, boxvstart, sizebox, sizebox);
                    g2.fillRect(boxhstart+(sizebox+5)*4, boxvstart, sizebox, sizebox);
                }
                break;
            default:
                g2.drawImage(imagecasan, 680, 100, this);
                break;
        }

        
        g2.setColor(Color.white);

        if(mapeado == false){
            int i = 0;
            int j = 0;

            // liga a matriz da fila de espera com as coordenadas na tela
            for(int x = 670; x < (maxScreenRow)*(tileSize+2)+300; x+=tileSize+2){
                for(int y = 440; y< (maxScreenCol)*(tileSize+2); y+=tileSize+2){
                    matrizBanheiro[i][j] = new Bloco();
                    matrizBanheiro[i][j].setX(x);
                    matrizBanheiro[i][j].setY(y);
                    matrizBanheiro[i][j].setFlag(3);
                    g2.fillRect(x, y, tileSize, tileSize);
                    j++;
                    if(j == 10){
                        break;
                    }
                }
                i++;
                if(i == 10){
                    break;
                }
                j = 0;
            }

            i = 0;
            j = 0;

            // liga a matriz do banheiro com as coordenadas na tela
            for(int x = 2; x < (maxScreenRow)*(tileSize+2); x+=tileSize+2){
                for(int y = 2; y< (maxScreenCol)*(tileSize+2); y+=tileSize+2){
                    matrizJogo[i][j] = new Bloco();
                    matrizJogo[i][j].setX(x);
                    matrizJogo[i][j].setY(y);
                    matrizJogo[i][j].setFlag(3);
                    g2.fillRect(x, y, tileSize, tileSize);
                    j++;
                }
                i++;
                j = 0;
            }


        for(int x = 0; x< 30; x++){
            for(int y = 0; y<30; y++){
                matrizJogo[x][y].setFlag(3);
            }
        }

        for(int x = 0; x< 10; x++){
            for(int y = 0; y<10; y++){
                matrizBanheiro[x][y].setFlag(3);
            }
        }

            mapeiaarray();


            mapeado = true;
        }
        else{
            mapeiaarray();
            if(getGameState()){
                // pinta tela com informacoes dos blocos da lista de espera
                for(int x = 0; x < maxScreenRow; x++){
                    for(int y = 0; y< maxScreenCol; y++){
                        if(matrizJogo[x][y].getFlag() == 0){
                            g2.setColor(Color.blue);
                            g2.fillRect(matrizJogo[x][y].getX(), matrizJogo[x][y].getY(), tileSize, tileSize);
                        }
                        else if(matrizJogo[x][y].getFlag() == 1){
                            g2.setColor(Color.red);
                            g2.fillRect(matrizJogo[x][y].getX(), matrizJogo[x][y].getY(), tileSize, tileSize);
                        }
                        else if(matrizJogo[x][y].getFlag() == 2){
                            g2.setColor(Color.GRAY);
                            g2.fillRect(matrizJogo[x][y].getX(), matrizJogo[x][y].getY(), tileSize, tileSize);
                        }
                        else{
                            g2.setColor(Color.white);
                            g2.fillRect(matrizJogo[x][y].getX(), matrizJogo[x][y].getY(), tileSize, tileSize);
                        }
                    }
                }

                // pinta tela com informacoes dos blocos da lista do banheiro
                for(int x = 0; x < 10; x++){
                    for(int y = 0; y< 10; y++){
                        if(matrizBanheiro[x][y].getFlag() == 0){
                            g2.setColor(Color.blue);
                            g2.fillRect(matrizBanheiro[x][y].getX(), matrizBanheiro[x][y].getY(), tileSize, tileSize);
                        }
                        else if(matrizBanheiro[x][y].getFlag() == 1){
                            g2.setColor(Color.red);
                            g2.fillRect(matrizBanheiro[x][y].getX(), matrizBanheiro[x][y].getY(), tileSize, tileSize);
                        }
                        else if(matrizBanheiro[x][y].getFlag() == 2){
                            g2.setColor(Color.GRAY);
                            g2.fillRect(matrizBanheiro[x][y].getX(), matrizBanheiro[x][y].getY(), tileSize, tileSize);
                        }
                        else{
                            g2.setColor(Color.white);
                            g2.fillRect(matrizBanheiro[x][y].getX(), matrizBanheiro[x][y].getY(), tileSize, tileSize);
                        }
                    }
                }


                g2.dispose();
            }
            else{
                for(int x = 0; x < maxScreenRow; x++){
                    for(int y = 0; y< maxScreenCol; y++){
                        if(matrizJogo[x][y].getFlag() == 0){
                            g2.setColor(Color.blue);
                            g2.fillRect(matrizJogo[x][y].getX(), matrizJogo[x][y].getY(), tileSize, tileSize);
                        }
                        else if(matrizJogo[x][y].getFlag() == 1){
                            g2.setColor(Color.red);
                            g2.fillRect(matrizJogo[x][y].getX(), matrizJogo[x][y].getY(), tileSize, tileSize);
                        }
                        else if(matrizJogo[x][y].getFlag() == 2){
                            g2.setColor(Color.GRAY);
                            g2.fillRect(matrizJogo[x][y].getX(), matrizJogo[x][y].getY(), tileSize, tileSize);
                        }
                        else{
                            g2.setColor(Color.white);
                            g2.fillRect(matrizJogo[x][y].getX(), matrizJogo[x][y].getY(), tileSize, tileSize);
                        }
                    }
                }


                // pinta tela com informacoes dos blocos da lista do banheiro
                for(int x = 0; x < 10; x++){
                    for(int y = 0; y< 10; y++){
                        if(matrizBanheiro[x][y].getFlag() == 0){
                            g2.setColor(Color.blue);
                            g2.fillRect(matrizBanheiro[x][y].getX(), matrizBanheiro[x][y].getY(), tileSize, tileSize);
                        }
                        else if(matrizBanheiro[x][y].getFlag() == 1){
                            g2.setColor(Color.red);
                            g2.fillRect(matrizBanheiro[x][y].getX(), matrizBanheiro[x][y].getY(), tileSize, tileSize);
                        }
                        else if(matrizBanheiro[x][y].getFlag() == 2){
                            g2.setColor(Color.GRAY);
                            g2.fillRect(matrizBanheiro[x][y].getX(), matrizBanheiro[x][y].getY(), tileSize, tileSize);
                        }
                        else{
                            g2.setColor(Color.white);
                            g2.fillRect(matrizBanheiro[x][y].getX(), matrizBanheiro[x][y].getY(), tileSize, tileSize);
                        }
                    }
                }


            }
        }

        g2.dispose();

    }


}