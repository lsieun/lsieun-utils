package lsieun.box;


import lsieun.box.canvas.Canvas;
import lsieun.box.canvas.Drawable;
import lsieun.box.table.OneLineTable;

public class BoxUtils {
    public static void printTable(String[][] matrix) {
        Drawable drawable = new OneLineTable(matrix);
        Canvas canvas = new Canvas();
        canvas.draw(0, 0, drawable);
        canvas.rectifyPosition();
        String str = canvas.toString();
        System.out.println(str);
    }
}
