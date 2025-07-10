package org.RestlessTech.TerminaLingo;

import java.util.List;

public class Lesson {
    private String numeLectie;
    private String dificultate;
    private List<String> cuvinteInvatate;
    private List<Test> teste;

    // Getters and Setters (REQUIRED for Jackson)
    public String getNumeLectie() {
        return numeLectie;
    }

    public void setNumeLectie(String numeLectie) {
        this.numeLectie = numeLectie;
    }

    public String getDificultate() {
        return dificultate;
    }

    public void setDificultate(String dificultate) {
        this.dificultate = dificultate;
    }

    public List<String> getCuvinteInvatate() {
        return cuvinteInvatate;
    }

    public void setCuvinteInvatate(List<String> cuvinteInvatate) {
        this.cuvinteInvatate = cuvinteInvatate;
    }

    public List<Test> getTeste() {
        return teste;
    }

    public void setTeste(List<Test> teste) {
        this.teste = teste;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "numeLectie='" + numeLectie + '\'' +
                ", dificultate='" + dificultate + '\'' +
                ", cuvinteInvatate=" + cuvinteInvatate +
                ", teste=" + teste +
                '}';
    }
}
