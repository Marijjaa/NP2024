import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/*
Потребно е да се развие програма за чување на информации за сите одобрени апликации за кредит во рамки на одредена банка.
Банката нуди 2 типа на кредит: потрошувачки и станбен. Разликата помеѓу едниот и другиот тип е во пресметката на каматната стапка,
како и во процесот на одобрување на кредитот.
Во банката работат вработени (bank clerks) коишто се одговорни за апликациите на клиентите. Ваквите вработени одлучуваат дали една
апликација е одобрена според правилото:
Кредитот се одобрува доколку сумата која ја позајмува клиентот не е повеќе од пет пати поголема од моменталната состојба на сметката
на клиентот кој аплицира за потпошувачки кредит.
Доколку клиентот аплицира за станбен кредит сумата која се позајмува не смее да биде повеќе од петнаесет пати поголема од
моменталната состојба на сметката на клиентот.
Ако кредитот не е одобрен, потребно е да се фрли исклучок од тип InvalidLoanApplicationException со порака во формат како во тест
примерите. Во ваков случај, одбиената апликација не се чува од страна на вработениот (bank clerk). Дополнително, фрлањето
на исклучокот не смее да го попречи додавањето на нови одобрени апликации за кредит.
За секоја одобрена апликација за кредит, може да се пресмета вкупната сума која клиентот ќе ја врати на банката според формулата:
totalPayment = loanAmount * interestRate / 100 * yearsOfPayment + loanAmount
Така, за секоја одобрена апликација, bank clerks добиваат надоместок (commission) oд 3% од totalPayment доколку станува збор за
потрошувачки кредит, односно 6% од totalPayment доколку станува збор за станбен кредит.
За да се имплементира ваквото сценарио дефинирајте класа Bank со следните методи:
конструктор без аргументи
метод void readApplications (InputStream is) - кој ги вчитува податоците за апликациите за кредити на вработените (bank clerks).
Во секој ред се дадени информациите за еден bank clerk во следниот формат:
bankClerkId [loanApplication1] [loanApplication2] .... [loanApplicationN].
Форматот на информациите за секоја апликација за кредит е следен:
clientId loanAmount yearsOfPayment clientBalance interestRate loanType, каде што loanType е карактер H или S што го означува
типот на кредитот (H - housing, S - spending). loanAmount е сумата која ја зајмува клиентот, додека пак clientBalance е
состојбата на неговата сметка (двата податоци се од тип int). interestRate е каматната стапка на кредитот (од тип double).
Сите информации во рамки на една линија од input-от се одделени со празно место.

метод void printApplicationsReport (OutputStream os) - кој ги печати податоците за апликациите, сортирани според бројот на
одобрени апликации, па според bankClerkId во растечки редослед, во следниот формат:
bankClerkId numberOfApprovedApplications minApplicationLoanValue maxApplicationTotalPayment totalCommission.
 */

class InvalidLoanApplicationException extends Exception{
    public InvalidLoanApplicationException(String message){
        super(message);
    }
}
abstract class CreditApplication{
    String clientId;
    double loanAmount;
    int yearsOfPayment;
    double clientsBalance;
    double interestRate;

    public CreditApplication(String clientId, double loanAmount, int yearsOfPayment, double clientsBalance, double interestRate) {
        this.clientId = clientId;
        this.loanAmount = loanAmount;
        this.yearsOfPayment = yearsOfPayment;
        this.clientsBalance = clientsBalance;
        this.interestRate = interestRate;
    }
    public double totalPayment(){
        return loanAmount * interestRate / 100 * yearsOfPayment + loanAmount;
    }
    abstract public boolean isApproved() throws InvalidLoanApplicationException;
    abstract public double getRate();
    public double getLoan() {
        return loanAmount;
    }
}
class HousingCreditApplication extends CreditApplication{

    public HousingCreditApplication(String clientId, double loanAmount, int yearsOfPayment, double clientsBalance, double interestRate) {
        super(clientId, loanAmount, yearsOfPayment, clientsBalance, interestRate);
    }

    @Override
    public boolean isApproved() {
        return loanAmount<=(clientsBalance*15);
    }

    @Override
    public double getRate() {
        return totalPayment()*0.06;
    }


}
class SpendingCreditApplication extends CreditApplication{

    public SpendingCreditApplication(String clientId, double loanAmount, int yearsOfPayment, double clientsBalance, double interestRate){
        super(clientId, loanAmount, yearsOfPayment, clientsBalance, interestRate);
    }

    @Override
    public boolean isApproved() throws InvalidLoanApplicationException {
        if(loanAmount<=(clientsBalance*5)){
            return true;
        }else{
            throw new InvalidLoanApplicationException("Not approved");
        }
    }
    @Override
    public double getRate() {
        return totalPayment()*0.03;
    }
}
class BankClerk{
    String id;
    List<CreditApplication> creditApplications;
    public BankClerk(String s){
        creditApplications=new ArrayList<>();
        String[] parts = s.split(" ");
        id = parts[0];
        for(int i=1; i<parts.length; i+=6){
            String cId = parts[i];
            double amount = Double.parseDouble(parts[i+1]);
            int years = Integer.parseInt(parts[i+2]);
            double balance = Double.parseDouble(parts[i+3]);
            double rate = Double.parseDouble(parts[i+4]);
            String loanType = parts[i+5];
            try {
                CreditApplication app = null;
                if (loanType.equals("H")) {
                    app = new HousingCreditApplication(cId, amount, years, balance, rate);
                } else if (loanType.equals("S")) {
                    app = new SpendingCreditApplication(cId, amount, years, balance, rate);
                }
                if (app.isApproved()){
                    creditApplications.add(app);
                }
            } catch (InvalidLoanApplicationException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public int getSize(){
        return creditApplications.size();
    }

    @Override
    public String toString() {
        double min = creditApplications.stream()
                .mapToDouble(CreditApplication::getLoan)
                .min()
                .orElse(0);
        double max = creditApplications.stream()
                .mapToDouble(CreditApplication::totalPayment)
                .max()
                .orElse(0);
        double commission = creditApplications.stream()
                .mapToDouble(CreditApplication::getRate)
                .sum();
        return String.format("%s %d %.2f %.2f %.2f", id, creditApplications.size(), min, max, commission);
    }

    public String getId() {
        return id;
    }
}
class Bank{
    List<BankClerk> bankClerks;
    public Bank(){
        bankClerks=new ArrayList<>();
    }

    public void readApplication(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        bankClerks = br.lines().map(BankClerk::new).collect(Collectors.toList());
    }

    public void printApplicationsReport(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        bankClerks.stream()
                .sorted(Comparator.comparing(BankClerk::getSize)
                .reversed().thenComparing(BankClerk::getId))
                .forEach(pw::println);
        pw.flush();
    }
}

public class BankTest {
    public static void main(String[] args) {
        Bank bank = new Bank();
        System.out.println("----- READING LOAN APPLICATIONS -----");
        bank.readApplication(System.in);
        System.out.println("----- PRINTING APPROVED APPLICATIONS REPORTS FOR BANK CLERKS -----");
        bank.printApplicationsReport(System.out);
    }
}