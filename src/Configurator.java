import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Configurator {
    private String file;
    private final ArrayList<String> algorithms = new ArrayList<>();
    private final ArrayList<Long> seeds = new ArrayList<>();
    private final ArrayList<Integer> population = new ArrayList<>();
    private int greedyRandomSize;
    private final ArrayList<Integer> alfa = new ArrayList<>();
    private final ArrayList<Integer> beta = new ArrayList<>();
    private int q0;
    private double probability;
    private double InitialPheromone;
    private int evaluations;
    private int time;
    private Boolean consoleLog;
    private double randomRate;
    private double greedyRate;
    private int edPopulation;
    private int edKBest;
    private int threads;
    private int individualsEDB;

    public Configurator(String path) throws IOException {
        String line;
        FileReader f;
        f = new FileReader(path);
        BufferedReader b = new BufferedReader(f);

        while ((line = b.readLine()) != null) {
            String[] splited = line.split("=");
            switch (splited[0]) {
                case "Algorithm":
                    String[] vAlgorithms = splited[1].split(" ");
                    Collections.addAll(algorithms, vAlgorithms);
                    break;
                case "Files":
                    file = splited[1];
                    break;
                case "Seeds":
                    String[] vSeeds = splited[1].split(" ");
                    for (String vSeed : vSeeds) {
                        seeds.add(Long.parseLong(vSeed));
                    }
                    break;
                case "Population":
                    String[] vPopulations = splited[1].split(" ");
                    for (String vPopulation : vPopulations) {
                        population.add(Integer.parseInt(vPopulation));
                    }
                    break;
                case "GreedyRandomSize":
                    greedyRandomSize = Integer.parseInt(splited[1]);
                    break;
                case "Alfa":
                    String[] vElites = splited[1].split(" ");
                    for (String vElite : vElites) {
                        alfa.add(Integer.parseInt(vElite));
                    }
                    break;
                case "Beta":
                    String[] vkBests = splited[1].split(" ");
                    for (String vkBest : vkBests) {
                        beta.add(Integer.parseInt(vkBest));
                    }
                    break;
                case "q0":
                    q0 = Integer.parseInt(splited[1]);
                    break;
                case "Probability":
                    probability = Double.parseDouble(splited[1]);
                    break;
                case "InitialPheromone":
                    InitialPheromone = Double.parseDouble(splited[1]);
                    break;
                case "Evaluations":
                    evaluations = Integer.parseInt(splited[1]);
                    break;
                case "Time":
                    time = Integer.parseInt(splited[1]);
                    break;
                case "ConsoleLog":
                    consoleLog = Boolean.parseBoolean(splited[1]);
                    break;
                case "RandomRate":
                    randomRate = Double.parseDouble(splited[1]);
                    break;
                case "GreedyRate":
                    greedyRate = Double.parseDouble(splited[1]);
                    break;
                case "Threads":
                    threads = Integer.parseInt(splited[1]);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + splited[0]);
            }
        }
    }

    public String getFile() {
        return file;
    }

    public ArrayList<Long> getSeeds() {
        return seeds;
    }

    public ArrayList<Integer> getPopulation() {
        return population;
    }

    public ArrayList<String> getAlgorithms() {
        return algorithms;
    }

    public int getGreedyRandomSize() {
        return greedyRandomSize;
    }

    public ArrayList<Integer> getAlfa() {
        return alfa;
    }

    public ArrayList<Integer> getBeta() {
        return beta;
    }

    public int getQ0() {
        return q0;
    }

    public double getProbability() {
        return probability;
    }

    public double getInitialPheromone() {
        return InitialPheromone;
    }

    public int getEvaluations() {
        return evaluations;
    }

    public int getTime() {
        return time;
    }

    public Boolean getConsoleLog() {
        return consoleLog;
    }

    public double getRandomRate() {
        return randomRate;
    }

    public double getGreedyRate() {
        return greedyRate;
    }

    public int getEdPopulation() {
        return edPopulation;
    }

    public int getEdKBest() {
        return edKBest;
    }

    public int getThreads() {
        return threads;
    }

    public int getIndividualsEDB() {
        return individualsEDB;
    }
}