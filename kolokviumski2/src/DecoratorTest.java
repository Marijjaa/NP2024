
interface Beverage {
    String getDescription();

    double getCost();
}

class Espresso implements Beverage {
    @Override
    public String getDescription() {
        return "Espresso";
    }

    @Override
    public double getCost() {
        return 80;
    }
}

class DarkRoasted implements Beverage {

    @Override
    public String getDescription() {
        return "DarkRoasted";
    }

    @Override
    public double getCost() {
        return 90;
    }
}

abstract class CondimentDecorator implements Beverage {
    Beverage beverage;

    public CondimentDecorator(Beverage beverage) {
        this.beverage = beverage;
    }
    public String description(){
        return beverage.getDescription();
    }
    public double cost(){
        return beverage.getCost();
    }
}

class Mocha extends CondimentDecorator {
    public Mocha(Beverage beverage) {
        super(beverage);
    }

    @Override
    public double getCost() {
        return beverage.getCost()+20;
    }

    @Override
    public String getDescription() {
        return beverage.getDescription() + ", Mocha";
    }
}

class Milk extends CondimentDecorator {
    public Milk(Beverage beverage) {
        super(beverage);
    }

    @Override
    public double getCost() {
        return beverage.getCost()+10;
    }

    @Override
    public String getDescription() {
        return beverage.getDescription()+", Milk";
    }
}

public class DecoratorTest {
    public static void main(String[] args) {
        Beverage espressoWithMilk = new Espresso();
        System.out.println(espressoWithMilk.getCost());
        System.out.println(espressoWithMilk.getDescription());

        espressoWithMilk = new Milk(espressoWithMilk);
        System.out.println(espressoWithMilk.getCost());
        System.out.println(espressoWithMilk.getDescription());
        ////////////////////////////////////////////////////
        Beverage darkWithMilk = new DarkRoasted();
        System.out.println(darkWithMilk.getCost());

        darkWithMilk = new Milk(darkWithMilk);
        System.out.println(darkWithMilk.getCost());
    }

}
