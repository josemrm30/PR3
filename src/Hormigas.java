import java.lang.reflect.Array;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Hormigas {


    private static Logger log;
    private ArrayList<Integer> s;

    public ArrayList<Integer> getS() {
        return s;
    }

    public static void Hormigas(double[][] dist,
                                int n, ArrayList<Integer> s, long iteraciones, int poblacion,
                                double greedy, int alfa, int beta, double q0, double p,
                                double fi) {

        ArrayList<ArrayList<Double>> feromona = new ArrayList<>();
        ArrayList<ArrayList<Double>> heuristica = new ArrayList<>();
        ArrayList<ArrayList<Integer>> hormigas = new ArrayList<>();
        ArrayList<Double> costes = new ArrayList<>();
        ArrayList<ArrayList<Boolean>> marcados = new ArrayList<>();

        double mejorCosteActual, mejorCosteGlobal = Double.MAX_VALUE;
        ArrayList<Integer> mejorHormigaActual = new ArrayList<>();

        // Inicializamos las matrices y vectores
        for (int i = 0; i < n; i++) {
            feromona.add(new ArrayList<>(Arrays.asList(new Double[n])));
            heuristica.add(new ArrayList<>(Arrays.asList(new Double[n])));
        }
        for (int i = 0; i < poblacion; i++) {
            hormigas.add(new ArrayList<>(Arrays.asList(new Integer[n])));
            marcados.add(new ArrayList<>(Arrays.asList(new Boolean[n])));
        }

        // Carga inicial de feromona y heurística
        double fInicial = 1.0 / (poblacion * greedy);
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (i != j) {
                    feromona.get(j).set(i, fInicial);
                    feromona.get(i).set(j, fInicial);
                    heuristica.get(j).set(i, 1.0 / dist[i][j]);
                    heuristica.get(i).set(j, 1.0 / dist[i][j]);
                }
            }
        }

        // Contador de iteraciones del sistema
        int cont = 0;
        long tiempo = 0;
        //Timer t = new Timer();

        // Principal: Comienzan las iteraciones
        while (cont < iteraciones && tiempo < 600000) {
            //t.start();

            // Carga de las hormigas iniciales
            for (int i = 0; i < poblacion; i++) {
                cargaInicial(hormigas.get(i), n, marcados.get(i));
            }

            // Generamos las n-1 componentes pendientes de las hormigas
            for (int comp = 1; comp < n; comp++) {
                for (int h = 0; h < poblacion; h++) {

                    // Elección del elemento de los no elegidos aún, a incluir en la solución
                    ArrayList<Double> ferxHeu = new ArrayList<>(Arrays.asList(new Double[n]));

                    // Cálculo de la cantidad total de feromonaxheuristica desde la ciudad actual al resto
                    for (int i = 0; i < n; i++) {
                        if (!marcados.get(h).get(i))
                            ferxHeu.set(i, Math.pow(heuristica.get(hormigas.get(h).get(comp - 1)).get(i), beta) *
                                    Math.pow(feromona.get(hormigas.get(h).get(comp - 1)).get(i), alfa));
                    }

                    // Cálculo del argMax y sumatoria del total de feromonaxHeuristica (denominador)
                    double denominador = 0.0;
                    double argMax = 0.0;
                    int posArgMax = 0;
                    for (int i = 0; i < n; i++) {
                        if (!marcados.get(h).get(i)) {
                            denominador += ferxHeu.get(i);
                            if (ferxHeu.get(i) > argMax) {
                                argMax = ferxHeu.get(i);
                                posArgMax = i;
                            }
                        }
                    }

                    // Función de transición
                    int elegido;
                    ArrayList<Double> prob = new ArrayList<>(Arrays.asList(new Double[n]));
                    double q = Math.random(); // aleatorio inicial

                    if (q0 <= q) { // aplicamos argumento máximo y nos quedamos con el mejor
                        elegido = posArgMax;
                    } else { // aplicamos regla de transición normal
                        for (int i = 0; i < n; i++) {
                            if (!marcados.get(h).get(i)) {
                                double numerador = ferxHeu.get(i);
                                prob.set(i, numerador / denominador);
                            }
                        }

                        // Elegimos la componente a añadir buscando en los intervalos de probabilidad
                        double uniforme = Math.random(); // aleatorio para regla de transición
                        double acumulado = 0.0;
                        elegido = -1;

                        for (int i = 0; i < n; i++) {
                            if (!marcados.get(h).get(i)) {
                                acumulado += prob.get(i);
                                if (uniforme <= acumulado) {
                                    elegido = i;
                                    break;
                                }
                            }
                        }
                    }

                    hormigas.get(h).set(comp, elegido);
                    marcados.get(h).set(elegido, true);
                }
            }

            // Aplicación de la actualización de feromona online
            for (int i = 0; i < poblacion; i++) {
                int comp = hormigas.get(i).size();
                feromona.get(hormigas.get(i).get(comp - 1)).set(hormigas.get(i).get(comp),
                        (1 - fi) * feromona.get(hormigas.get(i).get(comp - 1)).get(hormigas.get(i).get(comp)) +
                                fi * fInicial);
                feromona.get(hormigas.get(i).get(comp)).set(hormigas.get(i).get(comp - 1),
                        feromona.get(hormigas.get(i).get(comp - 1)).get(hormigas.get(i).get(comp)));
            }

            // Calculamos la mejor hormiga
            mejorCosteActual = Double.MAX_VALUE;
            for (int i = 0; i < poblacion; i++) {
                double coste = coste(hormigas.get(i), dist, n);
                if (coste < mejorCosteActual) {
                    mejorCosteActual = coste;
                    mejorHormigaActual = hormigas.get(i);
                }
            }

            // Actualizamos si la mejor actual mejora al mejor global
            if (mejorCosteActual < mejorCosteGlobal) {
                mejorCosteGlobal = mejorCosteActual;
                s = mejorHormigaActual;
            }

            // Aplicamos el demonio
            double deltaMejor = 1 / mejorCosteActual; // al ser minimización
            for (int i = 0; i < n - 1; i++) {
                feromona.get(mejorHormigaActual.get(i)).set(mejorHormigaActual.get(i + 1),
                        feromona.get(mejorHormigaActual.get(i)).get(mejorHormigaActual.get(i + 1)) + p * deltaMejor);
                feromona.get(mejorHormigaActual.get(i + 1)).set(mejorHormigaActual.get(i),
                        feromona.get(mejorHormigaActual.get(i)).get(mejorHormigaActual.get(i + 1))); // simétrica
            }

            // Se evapora en todos los arcos de la matriz de feromona
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        feromona.get(i).set(j, (1 - p) * feromona.get(i).get(j));
                    }
                }
            }

            // Limpiamos hormigas
            hormigas.clear();
            hormigas = new ArrayList<>(poblacion);
            for (int i = 0; i < poblacion; i++) {
                hormigas.add(new ArrayList<>(Arrays.asList(new Integer[n])));
            }
            costes.clear();
            costes = new ArrayList<>(poblacion);
            marcados.clear();
            marcados = new ArrayList<>(poblacion);
            for (int i = 0; i < poblacion; i++) {
                marcados.add(new ArrayList<>(Arrays.asList(new Boolean[n])));
            }

            cont++;

            if (cont % 100 == 0) {
                System.out.println("Iteracion: " + cont + " Coste: " + mejorCosteGlobal);
            }

            //t.stop();
            //tiempo += t.getElapsedTimeInMilliSec();
        }
        log.log(Level.INFO, "Solution = " + s + "  ");
        log.log(Level.INFO, "Total Iteraciones = " + cont + "  ");
        //System.out.println("Total Iteraciones:" + cont);
    }

    private static void cargaInicial(ArrayList<Integer> hormiga, int n, ArrayList<Boolean> marcados) {
        Random random = new Random();
        hormiga.set(0,random.nextInt(0, n-1));
        marcados.set(hormiga.get(0),true);
        // Implementa la lógica para la carga inicial de las hormigas
    }

    private static double coste(ArrayList<Integer> hormiga, double[][] dist, int n) {
        double cost=0.0;
        for (int i=0; i<n-1; i++){
            cost+= dist[hormiga.get(i)][hormiga.get(i+1)];
        }
        cost+=dist[hormiga.get(0)][hormiga.get(n-1)];  //cierro el ciclo
        return cost;

        // Implementa la lógica para calcular el coste de una hormiga
    }

}
