package analysers;

import language_elements.Sentence;

import java.util.HashMap;


public abstract class Analyser {
    protected Long time_needed = new Long(0);
    protected HashMap<String, Integer> stats = new HashMap<>();
    public abstract void analyse(Sentence sentence);

    public void getStatistic(){
        System.out.print(getClass().getSimpleName()+" ");
        stats.forEach((k, v) -> System.out.print(k + " : " + v+"  "));
        System.out.println(" ---- in " + time_needed + " ms");
    }
}
