import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
   public static void main(String[] args) {
      Result result = JUnitCore.runClasses(TestJunit.class);

       System.out.println("----End of Program Output-------------");
       System.out.println();
      for (Failure failure : result.getFailures()) {
         System.out.println(failure.toString());
      }
      System.out.println("Testing Output:");
      System.out.println("---------------------------------------");
      System.out.println("All TESTS Successful: "+result.wasSuccessful());

   }
}
