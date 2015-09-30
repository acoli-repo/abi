package helpers;



import enums.Column;
import enums.PrecisionRecallErrors;

import language_elements.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Evaluator {
    public static void eval(String filename, String ref_filename) throws IOException {

        Path file = Paths.get(filename);
        Path ref_file = Paths.get(ref_filename);
        BufferedReader filereader = getFileReader(file);
        BufferedReader ref_reader = getFileReader(ref_file);
        String line;
        String ref_line;
        String value;
        String ref_value;
        Column[] columns = Column.values();
        HashMap<PrecisionRecallErrors, HashMap> maps = new HashMap<>();
        for(PrecisionRecallErrors e : PrecisionRecallErrors.values()){
            HashMap<Column, Double> m = new HashMap<Column, Double>();
            for(Column c: columns){
                m.put(c, 0.0);
            }
            maps.put(e, m);
        }
        HashMap<Column, Double> true_negative = maps.get(PrecisionRecallErrors.TrueNegative);
        HashMap<Column, Double> true_positive = maps.get(PrecisionRecallErrors.TruePositive);
        HashMap<Column, Double> false_positive = maps.get(PrecisionRecallErrors.FalsePositive);
        HashMap<Column, Double> false_negative = maps.get(PrecisionRecallErrors.FalseNegative);
        int count = 0;

        while ((line = filereader.readLine()) != null && (ref_line = ref_reader.readLine()) != null && count <1000000 ){
            count++;
            Token token = new Token(line);
            Token ref_token = new Token(ref_line);

            for (Column c : columns) {
                value = token.getByColumn(c);
                ref_value = ref_token.getByColumn(c);

                if(value.equals(ref_value)){
                    if(value.equals("_")){
                        //beide haben keinen wert in der spalte -> true negative
                        true_negative.put(c,true_negative.get(c)+1);
                    }else{
                        //beide haben den gleichen wert in der spalte -> true positive
                        true_positive.put(c,true_positive.get(c)+1);
                    }
                }else{
                    if(ref_value.equals("_")){
                        //ref hat keinen wert und original hat einen wert -> false positive
                        false_positive.put(c,false_positive.get(c)+1);
                    }else if(value.equals("_")){
                        //ref hat einen wert aber original hat keinen wert -> false negative
                        false_negative.put(c,false_negative.get(c)+1);
                    }else{
                        //beide haben werte aber unterschiedliche -> false positive
                        false_positive.put(c,false_positive.get(c)+1);
                    }
                }
            }
        }
        ref_reader.close();
        filereader.close();
        for(Column c : columns){
            for(PrecisionRecallErrors e : PrecisionRecallErrors.values()){
                Double eval = (Double) maps.get(e).get(c);
                System.out.println(c.name() + "- "+ e.name() + " : "+ eval);

            }
            Double truepos = (Double) maps.get(PrecisionRecallErrors.TruePositive).get(c);
            Double falsepos = (Double) maps.get(PrecisionRecallErrors.FalsePositive).get(c);
            Double falseneg = (Double) maps.get(PrecisionRecallErrors.FalseNegative).get(c);
            Double trueneg = (Double) maps.get(PrecisionRecallErrors.TrueNegative).get(c);
            Double acc;
            Double prec;
            Double recall;
            try{
                prec = (truepos/(truepos+falsepos));
            }catch(java.lang.ArithmeticException e){
                prec = -1.0;
            }
            try{
                acc = truepos/(truepos+falseneg);
            }catch(java.lang.ArithmeticException e){
                acc = -1.0;
            }
            try{
                recall = ((truepos+trueneg)/(truepos+falseneg+trueneg+falsepos));
            }catch(java.lang.ArithmeticException e){
                recall = -1.0;
            }

            System.out.println(c.name() + "- Precision: " + prec);
            System.out.println(c.name() + "- Accuracy: " + acc);
            System.out.println(c.name() + "- Recall: " + recall);
        }

    }

    private static BufferedReader getFileReader(Path file){
        try{
            BufferedReader reader = Files.newBufferedReader(file);
            return reader;
        } catch (IOException e) {
            return null;
        }
    }
}
