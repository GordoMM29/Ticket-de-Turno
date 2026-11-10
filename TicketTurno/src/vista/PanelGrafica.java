/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class PanelGrafica extends JPanel {

    private static final Color COLOR_PENDIENTE = new Color(0xE74C3C);
    private static final Color COLOR_RESUELTO = new Color(0x27AE60);

    private int pendientes;
    private int resueltos;

    public PanelGrafica() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(480, 300));
    }

    public void setDatos(int pendientes, int resueltos) {
        this.pendientes = pendientes;
        this.resueltos = resueltos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();
        int total = pendientes + resueltos;

        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        centrar(g2, "Solicitudes por estatus", ancho / 2, 24, Color.DARK_GRAY);

        if (total == 0) {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            centrar(g2, "No hay solicitudes para mostrar", ancho / 2, alto / 2, Color.GRAY);
            g2.dispose();
            return;
        }

        int izq = 50;
        int der = 30;
        int sup = 45;
        int inf = 50;
        int areaAncho = ancho - izq - der;
        int areaAlto = alto - sup - inf;
        int base = sup + areaAlto;

        // Escala del eje Y (4 divisiones con numeros enteros)
        int maximo = Math.max(pendientes, resueltos);
        int tope = maximo <= 4 ? 4 : ((maximo + 3) / 4) * 4;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 0; i <= 4; i++) {
            int y = base - (areaAlto * i) / 4;
            g2.setColor(new Color(0xE0E0E0));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(izq, y, izq + areaAncho, y);
            String marca = String.valueOf((tope * i) / 4);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Color.GRAY);
            g2.drawString(marca, izq - 8 - fm.stringWidth(marca), y + fm.getAscent() / 2 - 1);
        }

        // Barras
        int anchoBarra = areaAncho / 5;
        dibujarBarra(g2, izq + areaAncho / 4 - anchoBarra / 2, base, anchoBarra, areaAlto, tope,
                pendientes, total, COLOR_PENDIENTE, "Pendiente");
        dibujarBarra(g2, izq + (areaAncho * 3) / 4 - anchoBarra / 2, base, anchoBarra, areaAlto, tope,
                resueltos, total, COLOR_RESUELTO, "Resuelto");

        // Eje base
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(izq, base, izq + areaAncho, base);
        g2.dispose();
    }

    private void dibujarBarra(Graphics2D g2, int x, int base, int anchoBarra, int areaAlto, int tope,
            int valor, int total, Color color, String etiqueta) {
        int alturaBarra = (int) Math.round((double) valor / tope * areaAlto);
        if (valor > 0 && alturaBarra < 2) {
            alturaBarra = 2;
        }
        g2.setColor(color);
        g2.fillRect(x, base - alturaBarra, anchoBarra, alturaBarra);

        int porcentaje = (int) Math.round(valor * 100.0 / total);
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        centrar(g2, valor + " (" + porcentaje + "%)", x + anchoBarra / 2, base - alturaBarra - 8, Color.DARK_GRAY);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        centrar(g2, etiqueta, x + anchoBarra / 2, base + 22, Color.DARK_GRAY);
    }

    private void centrar(Graphics2D g2, String texto, int xCentro, int y, Color color) {
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(color);
        g2.drawString(texto, xCentro - fm.stringWidth(texto) / 2, y);
    }
}