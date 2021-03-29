package cz.zcu.kiv.crce.classmodel.processor;

import java.util.ArrayList;
import cz.zcu.kiv.crce.classmodel.processor.Variable.VariableType;



public class VariablesContainer {
    private ArrayList<Variable> vars;


    public Variable get(int index) {
        if (vars.size() <= index) {
            return null;
        }
        return vars.get(index);
    }

    public Variable init(int index) {
        if (index >= vars.size()) {
            Variable last = null;
            final int numIterations = index - vars.size();
            for (int i = 0; i <= numIterations; i++) {
                last = new Variable().setType(VariableType.OTHER);
                this.vars.add(last);
            }
            return last;
        }
        return vars.get(index);
    }

    public void set(int index, Variable var) {
        if (index == vars.size()) {
            vars.add(var);
        } else if (index - vars.size() >= 1) {
            Variable last = null;
            final int numIterations = index - vars.size();
            for (int i = 0; i <= numIterations; i++) {
                last = new Variable().setType(VariableType.OTHER);
                this.vars.add(last);
            }
            vars.set(index, var);
        } else {
            vars.set(index, var).setType(VariableType.SIMPLE);
        }
    }

    /**
     * @param vars
     */
    public VariablesContainer() {
        vars = new ArrayList<>();
        // TODO: dont do this -> need for detection of static or nonstatic class
        vars.add(new Variable("").setType(VariableType.OTHER));
    }
}
