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
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Pallag Jonatan
 */
class Vektor {

    double x, y;

    public Vektor() {
        x = 0;
        y = 0;
    }

    public Vektor(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vektor(Vektor xy) {
        this.x = xy.x;
        this.y = xy.y;
    }

}

class SebessegVektor {

    double from_x, from_y;
    double to_x, to_y;

    public SebessegVektor(double from_x, double from_y, double to_x, double to_y) {
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
    Color color = Color.WHITE;

    public Bolygo(int m, Color color, double x, double y) {
        this.m = m;
        this.color = color;

        init_sebesseg = new SebessegVektor(x, y, x, y);

        pozicio.x = init_sebesseg.from_x;
        pozicio.y = init_sebesseg.from_y;

        next_pozicio.x = init_sebesseg.from_x;
        next_pozicio.y = init_sebesseg.from_y;

        sebesseg.x = (init_sebesseg.to_x - init_sebesseg.from_x);
        sebesseg.y = (init_sebesseg.to_y - init_sebesseg.from_y);

    }

    public void modositSebesseg(double to_x, double to_y) {
        init_sebesseg.to_x = to_x;
        init_sebesseg.to_y = to_y;

        sebesseg.x = (init_sebesseg.to_x - init_sebesseg.from_x);
        sebesseg.y = (init_sebesseg.to_y - init_sebesseg.from_y);
    }

    public void rajzol(Graphics g, Kamera kamera) {
        Vektor pozicio = new Vektor(kamera.toKepernyo(this.pozicio));

        g.setColor(color);

        int d = kamera.toKepernyoLength(m);

        g.fillOval((int) (pozicio.x - d / 2), (int) (pozicio.y - d / 2), d, d);

        g.setColor(Color.WHITE);
        Vektor sebesseghely = kamera.toKepernyoHely(sebesseg.x, sebesseg.y);
        g.drawLine((int) pozicio.x, (int) pozicio.y, (int) pozicio.x + (int) sebesseghely.x, (int) pozicio.y + (int) sebesseghely.y);
    }

    public void nextStep(List<Bolygo> bolygok) {
        //Erõk
        double t = 30 / 100.0;
        double Fx = 0;
        double Fy = 0;

        for (Bolygo bolygo : bolygok) {

            double r = Point.distance(bolygo.pozicio.x, bolygo.pozicio.y, this.pozicio.x, this.pozicio.y);
            if (r != 0) {

                double gamma = 6.66743;//* Math.pow(10, -11);
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

class Kamera {

    Vektor sarok;
    double szelesseg;

    int w, h;

    public Kamera(int w, int h) {
        this.w = w;
        this.h = h;
        sarok = new Vektor(500, 500);
        szelesseg = 1920;
    }

    Vektor toUniverzum(int x, int y) {
        double xU = x * szelesseg / w + sarok.x;
        double yU = y * szelesseg / w + sarok.y;
        return new Vektor(xU, yU);
    }

    Vektor toKepernyo(double xU, double yU) {
        double x = (xU - sarok.x) * w / szelesseg;
        double y = (yU - sarok.y) * w / szelesseg;
        return new Vektor(x, y);
    }

    Vektor toKepernyo(Vektor xy) {
        return toKepernyo(xy.x, xy.y);
    }

    Vektor toUniverzumHely(int x, int y) {
        double xU = x * szelesseg / w;
        double yU = y * szelesseg / w;
        return new Vektor(xU, yU);
    }

    double toUniverzumLength(int l) {
        return l * szelesseg / w;
    }

    int toKepernyoLength(double lU) {
        return (int) (lU * w / szelesseg);
    }

    Vektor toKepernyoHely(double xU, double yU) {
        double x = xU * w / szelesseg;
        double y = yU * w / szelesseg;
        return new Vektor(x, y);
    }

    void zoom(double a, int x, int y) {
        Vektor pont = this.toUniverzum(x, y);
        szelesseg *= a;
        sarok.x = (sarok.x - pont.x) * a + pont.x;
        sarok.y = (sarok.y - pont.y) * a + pont.y;
    }

    void eltol(int dx, int dy) {
        double dxU = dx * szelesseg / w;
        double dyU = dy * szelesseg / w;

        sarok.x = sarok.x - dxU;
        sarok.y = sarok.y - dyU;
    }

    void resize(int width, int height) {
        w = width;
        h = height;
    }
}

public class Panel extends JPanel {

    CopyOnWriteArrayList<Bolygo> lista = new CopyOnWriteArrayList<>();
    Bolygo akt_bolygo = null;
    Kamera kamera;

    public Panel() {
        super();

        kamera = new Kamera(100, 100);

        try {
            background = ImageIO.read(new File("src/naprendszerszimulator/backgrounds/background1.jpg"));
        } catch (IOException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        BufferedImage img_panel = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img_panel.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        kamera.resize(getWidth(), getHeight());

        g.setStroke(new BasicStroke(3));

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        drawBackground(g);

        for (int i = 0; i < lista.size(); i++) {
            lista.get(i).rajzol(g, kamera);
        }

        //Aktuális szakasz rajzolása
        if (akt_bolygo != null) {
            akt_bolygo.rajzol(g, kamera);
        }

        graphics.drawImage(img_panel, 0, 0, this);
    }

    private void drawBackground(Graphics2D g) {
        if (background != null) {
            double screen_rate = getHeight() / (double) getWidth();
            double background_rate = background.getHeight() / (double) background.getWidth();

            int w, h;

            if (screen_rate < background_rate) {
                w = getWidth();
                h = getWidth() * background.getHeight() / background.getWidth();
                g.drawImage(background, 0, (getHeight() - h) / 2, w, h, this);
            } else {
                w = getHeight() * background.getWidth() / background.getHeight();
                h = getHeight();
                g.drawImage(background, (getWidth() - w) / 2, 0, w, h, this);
            }

        }
    }

    void bolygoLetrehoz(int m, Color color, int x, int y) {
        Vektor poz = kamera.toUniverzum(x, y);
        akt_bolygo = new Bolygo(m, color, poz.x, poz.y);
    }

    void bolygoModosit(int x_akt, int y_akt) {
        if (akt_bolygo != null) {
            Vektor poz = kamera.toUniverzum(x_akt, y_akt);
            akt_bolygo.modositSebesseg(poz.x, poz.y);
        }
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

    Kamera getKamera() {
        return kamera;
    }

    BufferedImage background = null;

    void setBackground(BufferedImage image) {
        background = image;
    }

}
