/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jonathan
 * @param <T>
 * @param <U>
 * 
 */
public class Pair<T,U> {
    public final T x;
    public final U y;

    public Pair(T x, U y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString(){
        return "["+x.toString() + "]["+ y.toString()+"]";
    }
}

