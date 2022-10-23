package src;

public class Bloco {
    private int x;
    private int y;
    private boolean radiacao;
    private boolean robo;
    private int flag; //0 sem flag, 1 vai morrer, 2 vai viver

    Bloco(){
        this.x = 0;
        this.y = 0;
        radiacao = false;
        robo = false;
        flag = 0;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isRadiacao() {
        return this.radiacao;
    }

    public boolean getRadiacao() {
        return this.radiacao;
    }

    public void setRobo(){
        this.robo = true;
    }

    public void removeRobo(){
        this.robo = false;
    }

    public boolean isRobo(){
        return this.robo;
    }

    public void setRadiacao(boolean radiacao) {
        this.radiacao = radiacao;
    }

}
