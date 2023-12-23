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

    public static void setLog(Logger logger){
        log= logger;
    }

    public static ArrayList<Boolean> inicializarVectorBool(int n){
        ArrayList<Boolean> vector = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            vector.add(false);
        }
        return vector;
    }

    public static ArrayList<Double> inicializarVectorDou(int n){
        ArrayList<Double> fila = new ArrayList<>();
        Random random = new Random();
        for (int j = 0; j < n; j++) {
            Double valorAleatorio = random.nextDouble() * 100;
            fila.add(valorAleatorio);
        }
        return fila;
    }
    public static ArrayList<Integer> inicializarVectorInt(int n){
        ArrayList<Integer> fila = new ArrayList<>();
        Random random = new Random();
        for (int j = 0; j < n; j++) {
            int valorAleatorio = random.nextInt() * 100;
            fila.add(valorAleatorio);
        }
        return fila;
    }
    public static ArrayList<ArrayList<Double>> inicializarMatrizDou(int n) {
        ArrayList<ArrayList<Double>> distancias = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            ArrayList<Double> fila = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                double valorAleatorio = random.nextDouble() * 100;
                fila.add(valorAleatorio);
            }
            distancias.add(fila);
        }
        return distancias;
    }

    public static ArrayList<ArrayList<Integer>> inicializarMatrizInt(int n) {
        ArrayList<ArrayList<Integer>> distancias = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            ArrayList<Integer> fila = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                Integer valorAleatorio = random.nextInt() * 100;
                fila.add(valorAleatorio);
            }
            distancias.add(fila);
        }
        return distancias;
    }

    public static ArrayList<ArrayList<Boolean>> inicializarMatrizBool(int n){
        ArrayList<ArrayList<Boolean>> matriz = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ArrayList<Boolean> fila = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                fila.add(false);
            }
            matriz.add(fila);
        }
        return matriz;
    }

    public static void cargaInicial(ArrayList<Integer> s, int n, ArrayList<Boolean> marcados) {
        Random random = new Random();
        int numeroAleatorio = random.nextInt(n);
        //System.out.println("valor n: " + n);
        //System.out.println("num Aleatorio: " + numeroAleatorio);

        s.set(0,numeroAleatorio);
        marcados.set(s.get(0), true);
    }

    public static double costo(ArrayList<Integer> s, ArrayList<ArrayList<Double>> distancias, int n) {
        double costo = 0.0;
        for (int i = 0; i < n - 1; i++) {
            costo += distancias.get(s.get(i)).get(s.get(i + 1));
        }
        costo += distancias.get(s.get(0)).get(s.get(n - 1)); // Cerrar el ciclo
        return costo;
    }

    public void hormigas(ArrayList<ArrayList<Double>> distancias, int numCiudades, ArrayList<Integer> mejorCamino, int iteraciones,
                                 int popuHormi, double greedy, int alfa, int beta, double q0, double p, double fi,Logger log) {
        System.out.println(log.getName());
        this.setLog(log);
        double mejorCosteActual, mejorCosteGlobal = Double.POSITIVE_INFINITY;

        // Inicializar matrices y listas

        ArrayList<ArrayList<Double>> feromona = inicializarMatrizDou(numCiudades);
        ArrayList<ArrayList<Double>> heuristica = inicializarMatrizDou(numCiudades);
        ArrayList<ArrayList<Integer>> hormigas = inicializarMatrizInt(popuHormi);
        ArrayList<ArrayList<Boolean>> marcados = inicializarMatrizBool(numCiudades);
        ArrayList<Integer> mejorHormigaActual = inicializarVectorInt(numCiudades);

        // Carga inicial de feromona y heurística
        double fInicial = 1.0 / (popuHormi * greedy);
        for (int i = 0; i < numCiudades - 1; i++) {//n - 1 por el j = i + 1
            for (int j = i + 1; j < numCiudades; j++) {
                if (i != j) {
                    feromona.get(j).set(i, fInicial);
                    feromona.get(i).set(j, fInicial);
                    heuristica.get(j).set(i, 1.0 / distancias.get(i).get(j));
                    heuristica.get(i).set(j, 1.0 / distancias.get(i).get(j));
                }
            }
        }

        int cont = 0;
        //long tiempoInicio = System.currentTimeMillis();

        while (cont < iteraciones ) {

            // Carga de hormigas iniciales
            for (int i = 0; i < popuHormi; i++) {
                cargaInicial(hormigas.get(i), numCiudades, marcados.get(i));
            }
            StringBuilder msg = new StringBuilder();
            // Generamos las n-1 componentes pendientes de las hormigas
            for (int comp = 1; comp < numCiudades; comp++) {
                // Para cada hormiga
                for (int indiHormi = 0; indiHormi < popuHormi; indiHormi++) {

                    // Elección del elemento de los no elegidos aún, a incluir en la solución
                    ArrayList<Double> ferxHeu = new ArrayList<>(numCiudades);
                    for (int i = 0; i < numCiudades; i++) {
                        ferxHeu.add(0.0);
                        if (!marcados.get(indiHormi).get(i))
                            ferxHeu.set(i, Math.pow(heuristica.get(hormigas.get(indiHormi).get(comp - 1)).get(i), beta) *
                                    Math.pow(feromona.get(hormigas.get(indiHormi).get(comp - 1)).get(i), alfa));
                    }

                    // Cálculo del argMax y sumatoria del total de feromona*heurística (denominador)
                    double denominador = 0.0;
                    double argMax = 0.0;
                    int posArgMax = 0;
                    for (int i = 0; i < numCiudades; i++) {
                        if (!marcados.get(indiHormi).get(i)) {
                            denominador += ferxHeu.get(i);
                            if (ferxHeu.get(i) > argMax) {
                                argMax = ferxHeu.get(i);
                                posArgMax = i;
                            }
                        }
                    }

                    // Función de transición
                    int elegido = -1;
                    ArrayList<Double> prob = new ArrayList<>(numCiudades);

                    Random rand = new Random();
                    double q = rand.nextDouble(); // Número aleatorio

                    if (q0 <= q) {
                        elegido = posArgMax;
                    } else {
                        for (int i = 0; i < numCiudades; i++) {
                            prob.add(0.0);
                            if (!marcados.get(indiHormi).get(i)) {
                                double numerador = ferxHeu.get(i);
                                prob.set(i, numerador / denominador);
                            }
                        }

                        // Elegimos la componente a añadir buscando en los intervalos de probabilidad
                        Random rand2 = new Random();
                        double uniforme = rand2.nextDouble(); // Número aleatorio para regla de transición
                        double acumulado = 0.0;

                        for (int i = 0; i < numCiudades; i++) {
                            if (!marcados.get(indiHormi).get(i)) {
                                acumulado += prob.get(i);
                                if (uniforme <= acumulado) {
                                    elegido = i;
                                    break;
                                }
                            }
                        }
                    }

                    if(elegido != -1) {
                        hormigas.get(indiHormi).set(comp, elegido);
                        marcados.get(indiHormi).set(elegido, true);
                    }
                }
                // Aplicación de la actualización de feromona online
                for (int i = 0; i < popuHormi; i++) {
                    feromona.get(hormigas.get(i).get(comp - 1)).set(hormigas.get(i).get(comp),
                            ((1 - fi) * feromona.get(hormigas.get(i).get(comp - 1)).get(hormigas.get(i).get(comp))) +
                                    (fi * fInicial));
                    feromona.get(hormigas.get(i).get(comp)).set(hormigas.get(i).get(comp - 1),
                            feromona.get(hormigas.get(i).get(comp - 1)).get(hormigas.get(i).get(comp))); // simétrica
                }


            }

            log.log(Level.INFO, "Iteration " + iteraciones);
            log.log(Level.INFO, msg.toString());

            // Calculamos la mejor hormiga
            mejorCosteActual = Double.POSITIVE_INFINITY;
            for (int i = 0; i < popuHormi; i++) {
                double coste = costo(hormigas.get(i), distancias, numCiudades);
                if (coste < mejorCosteActual) {
                    mejorCosteActual = coste;
                    mejorHormigaActual = new ArrayList<>(hormigas.get(i));
                }
            }

            // Actualizamos si la mejor actual mejora al mejor global
            if (mejorCosteActual < mejorCosteGlobal) {
                mejorCosteGlobal = mejorCosteActual;
                mejorCamino.clear();
                mejorCamino.addAll(mejorHormigaActual);
            }

            // Aplicamos el "demonio" (actualización de feromona)
            double deltaMejor = 1 / mejorCosteActual; // al ser minimización
            for (int i = 0; i < numCiudades - 1; i++) {
                feromona.get(mejorHormigaActual.get(i)).set(mejorHormigaActual.get(i + 1),
                        feromona.get(mejorHormigaActual.get(i)).get(mejorHormigaActual.get(i + 1)) + (p * deltaMejor));
                feromona.get(mejorHormigaActual.get(i + 1)).set(mejorHormigaActual.get(i),
                        feromona.get(mejorHormigaActual.get(i)).get(mejorHormigaActual.get(i + 1))); // simétrica
            }

            // Evaporación en todos los arcos de la matriz de feromona
            for (int i = 0; i < numCiudades; i++) {
                for (int j = 0; j < numCiudades; j++) {
                    if (i != j) {
                        feromona.get(i).set(j, (1 - p) * feromona.get(i).get(j));
                    }
                }
            }
            cont++;

            if (cont % 100 == 0) {
                //System.out.println("Iteracion: " + cont + " Coste: " + mejorCosteGlobal);
                log.log(Level.INFO,"Iteracion: " + cont);
                log.log(Level.INFO,"mejor Solucion: " + mejorCamino.toString() + " Coste: " + mejorCosteGlobal);
                log.log(Level.INFO, msg.toString());
            }
        }

        System.out.println("Total Iteraciones: " + cont);
    }
}
