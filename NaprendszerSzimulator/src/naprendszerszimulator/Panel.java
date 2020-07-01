/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naprendszerszimulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author Pallag Jonatan
 */
interface s {

    int fuggveny();

}

class SebessegVektor {

    int from_x, from_y;
    int to_x, to_y;

    public SebessegVektor(int from_x, int from_y, int to_x, int to_y) {
        this.from_x = from_x;
        this.from_y = from_y;
        this.to_x = to_x;
        this.to_y = to_y;
    }

    double getSebesseg() {
        return Point2D.distance(from_x, from_y, to_x, to_y);
    }
}

class Bolygo {

    SebessegVektor sebesseg;
    int m = 100;
    Color color = Color.PINK;

    public Bolygo(int m, Color color, int x, int y) {
        this.m = m;
        this.color = color;

        sebesseg = new SebessegVektor(x, y, x, y);

    }

    public void modositSebesseg(int to_x, int to_y) {
        sebesseg.to_x = to_x;
        sebesseg.to_y = to_y;
    }

    public void rajzol(Graphics g) {
        g.setColor(color);

        int d = m; //TODO konstans szorzó
        g.fillOval(sebesseg.from_x - d / 2, sebesseg.from_y - d / 2, d, d);

        g.setColor(Color.WHITE);

        g.drawLine(sebesseg.from_x, sebesseg.from_y, sebesseg.to_x, sebesseg.to_y);
    }
}

public class Panel extends JPanel {

    ArrayList<Bolygo> lista = new ArrayList();
    Bolygo akt_bolygo = null;

    public Panel() {
        super();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        ((Graphics2D) g).setStroke(new BasicStroke(3));
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        for (int i = 0; i < lista.size(); i++) {
            lista.get(i).rajzol(g);
        }

        //Aktuális szakasz rajzolása
        if (akt_bolygo != null) {
            akt_bolygo.rajzol(g);
        }
    }

    void bolygoLetrehoz(int m, Color color, int x, int y) {
        akt_bolygo = new Bolygo(m, color, x, y);
    }

    void bolygoModosit(int x_akt, int y_akt) {
        akt_bolygo.modositSebesseg(x_akt, y_akt);
    }

    void bolygoHozzaad() {
        lista.add(akt_bolygo);
        akt_bolygo = null;
    }

}
