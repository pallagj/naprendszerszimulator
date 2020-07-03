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
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author Pallag Jonatan
 */
class Vektor {

    double x, y;
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

    SebessegVektor init_sebesseg;

    Vektor sebesseg = new Vektor();
    Vektor pozicio = new Vektor();
    Vektor next_pozicio = new Vektor();

    int m = 100;
    Color color = Color.PINK;

    public Bolygo(int m, Color color, int x, int y) {
        this.m = m;
        this.color = color;

        init_sebesseg = new SebessegVektor(x, y, x, y);

        pozicio.x = init_sebesseg.from_x;
        pozicio.y = init_sebesseg.from_y;

        next_pozicio.x = init_sebesseg.from_x;
        next_pozicio.y = init_sebesseg.from_y;

        sebesseg.x = (init_sebesseg.to_x - init_sebesseg.from_x) / 1000000;
        sebesseg.y = (init_sebesseg.to_y - init_sebesseg.from_y) / 1000000;

    }

    public void modositSebesseg(int to_x, int to_y) {
        init_sebesseg.to_x = to_x;
        init_sebesseg.to_y = to_y;

        sebesseg.x = init_sebesseg.to_x - init_sebesseg.from_x;
        sebesseg.y = init_sebesseg.to_y - init_sebesseg.from_y;
    }

    public void rajzol(Graphics g) {
        g.setColor(color);

        int d = m; //TODO konstans szorzó
        g.fillOval((int) (pozicio.x - d / 2), (int) (pozicio.y - d / 2), d, d);

        g.setColor(Color.WHITE);

        g.drawLine((int) pozicio.x, (int) pozicio.y, (int) pozicio.x + (int) sebesseg.x, (int) pozicio.y + (int) sebesseg.y);
    }

    public void nextStep(ArrayList<Bolygo> bolygok) {
        //Erõk
        double t = 30 / 10;
        double Fx = 0;
        double Fy = 0;

        for (Bolygo bolygo : bolygok) {

            double r = Point.distance(bolygo.pozicio.x, bolygo.pozicio.y, this.pozicio.x, this.pozicio.y);
            if (r != 0) {

                double gamma = 6.6743;//* Math.pow(10, -11);
                double F = gamma * this.m * bolygo.m / (r * r);

                double vx = bolygo.pozicio.x - this.pozicio.x;
                double vy = bolygo.pozicio.y - this.pozicio.y;

                Fx += (vx / Point.distance(0, 0, vx, vy)) * F;
                Fy += (vy / Point.distance(0, 0, vx, vy)) * F;
            }
        }

        double ax = Fx / m;
        double ay = Fy / m;

        next_pozicio.x = pozicio.x + sebesseg.x * t + ax * t * t / 2;
        next_pozicio.y = pozicio.y + sebesseg.y * t + ay * t * t / 2;

        sebesseg.x += ax * t;
        sebesseg.y += ay * t;
    }

    public void frissitPozicio() {
        pozicio.x = next_pozicio.x;
        pozicio.y = next_pozicio.y;
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

    void nextFrame() {

        lista.forEach(bolygo -> bolygo.nextStep(lista));
        lista.forEach(bolygo -> bolygo.frissitPozicio());
    }

    void megsemmisit() {
        lista.clear();
    }

}
