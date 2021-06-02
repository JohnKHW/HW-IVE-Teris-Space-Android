package ivegsd.johnwkh.assignment;

import android.graphics.Point;

public class Vector2 {
    public float x, y;
    public Vector2(){

    }
    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }
    public Vector2(Point point){
        this.x = point.x;
        this.y = point.y;
    }

    public static Vector2 zero(){
        return  new Vector2();
    }

    public static  Vector2 one(){
        return new Vector2(1, 1);
    }

    public static Vector2 add(Vector2 v1, Vector2 v2){
        Vector2 result = new Vector2(v1.x + v2.x, v1.y + v2.y);
        return result;
    }

    public static Vector2 minus(Vector2 v1, Vector2 v2){
        Vector2 result = new Vector2(v1.x - v2.x, v1.y - v2.y);
        return result;
    }

    public static Vector2 plus(Vector2 v1, Vector2 v2){
        Vector2 result = new Vector2(v1.x * v2.x, v1.y * v2.y);
        return result;
    }

    public static Vector2 divide(Vector2 v1, Vector2 v2){
        Vector2 result = new Vector2(v1.x / v2.x, v1.y / v2.y);
        return result;
    }

    public static float dot(Vector2 v1, Vector2 v2){
        return v1.x * v2.x + v1.y * v2.y;
    }

    public Vector2 add(Vector2 other){
        x += other.x;
        y += other.y;
        return this;
    }

    public Vector2 minus(Vector2 other){
        x -= other.x;
        y -= other.y;
        return this;
    }

    public Vector2 plus(Vector2 other){
        x *= other.x;
        y *= other.y;
        return this;
    }

    public Vector2 divide(Vector2 other){
        x /= other.x;
        y /= other.y;
        return this;
    }

    public float dot(Vector2 other){
        return x * other.x + y * other.y;
    }

    public Point toPoint(){
        return new Point(Math.round(x), Math.round(y));
    }

    public String toString(){
        return "[x: " + x + ", y: "+ y +"]";
    }

}
