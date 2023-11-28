package MAP.domain;

import java.util.Objects;
public class Tuple<E1, E2> {

    private E1 e1;

    private E2 e2;

    public Tuple(){
        this.e1 = null;
        this.e2 = null;
    }

    public Tuple(E1 e1, E2 e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    public E1 getE1() {
        return e1;
    }

    public void setE1(E1 e1) {
        this.e1 = e1;
    }


    public E2 getE2() {
        return e2;
    }

    public void setE2(E2 e2) {
        this.e2 = e2;
    }

    @Override
    public String toString(){
        return e1 + ", " + e2;
    }

    @Override
    public boolean equals(Object obj) {
        return this.e1.equals(((Tuple) obj).e1) && this.e2.equals(((Tuple) obj).e2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }
}


