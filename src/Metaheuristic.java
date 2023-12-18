import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.*;

public class Metaheuristic implements Runnable {

    private Logger log;
    private final CountDownLatch cdl;
    private final LectorDatos data;
    private final Long seed;
    private int iterations;
    private final int alfa;
    private int beta;
    private final int population;

    private final int[][] cities;
    private final String alg;


    public Metaheuristic(String algorithm, CountDownLatch cdl, LectorDatos dat, Long seed, int iterations, int alfa, int beta, int population, int[][] citiesList) throws IOException {
        this.alg = algorithm;
        this.cdl = cdl;
        this.data = dat;
        this.seed = seed;
        this.iterations = iterations;
        this.alfa = alfa;
        this.beta = beta;
        this.population = population;
        this.cities = citiesList;
    }

    public Metaheuristic(String algorithm, CountDownLatch cdl, LectorDatos dat, Long seed, int alfa, int population, int[][] citiesList) throws IOException {
        this.alg = algorithm;
        this.cdl = cdl;
        this.data = dat;
        this.seed = seed;
        this.alfa = alfa;
        this.population = population;
        this.cities = citiesList;
    }

    public void createLogger(Long seed, int iterations, int alfa, int beta, int population) throws IOException {
        String logFile = "log/" + alg + "_" + Utils.config.getFile() + "_" + seed + "_P=" + population +
                "_I=" + iterations + "_alfa=" + alfa + "_beta=" + beta + "_q0=" + Utils.config.getQ0() +
                "_Prob" + Utils.config.getProbability() + "InitPher=" +Utils.config.getInitialPheromone() + ".txt";

        log = Logger.getLogger("Ant" + " " + logFile);
        if (Utils.config.getConsoleLog()) {
            ConsoleHandler consoleHand = new ConsoleHandler();
            log.addHandler(consoleHand);
        } else {
            FileHandler fileHand = new FileHandler(logFile);
            log.setUseParentHandlers(false);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHand.setFormatter(formatter);
            log.addHandler(fileHand);
        }
    }

    @Override
    public void run() {
        try {
            createLogger(seed, iterations, alfa, beta, population);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Integer actualEvaluations = 0;
        long initTime;
        long endTime;
        long diff;


        switch (alg) {
            case "Ants":
                //ALGGenOX2 genes = new ALGGenOX2(seed, elite, kBest, log, cities);
                //genes.initialization(population, data.getCiudades().length);
                initTime = System.currentTimeMillis();
                //actualEvaluations = genes.evaluation(genes.getPopulation(), actualEvaluations);
                while (actualEvaluations < Utils.config.getEvaluations() && ((System.currentTimeMillis() - initTime) / 1000) < Utils.config.getTime()) {


//                    genes.selection();
//
//                    genes.cross();
//
//                    genes.mutation();
//
//                    actualEvaluations = genes.evaluation(genes.getNewPopulation(), actualEvaluations);
//
//                    genes.replacement();
                }
                endTime = System.currentTimeMillis();
                diff = endTime - initTime;
                log.log(Level.INFO, "Run time = " + diff + " milliseconds. ");
                break;
        }

        cdl.countDown();
        for (Handler handler : log.getHandlers()) {
            handler.close();
        }
    }
}
