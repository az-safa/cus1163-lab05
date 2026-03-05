package academy.javapro.lab05;

import java.util.Scanner;


public class MathCalculatorLab {

    // Simple class to hold calculation data (PROVIDED - DO NOT MODIFY)
    static class CalculatorBase {
        int n;
        long result;

        public CalculatorBase(int n) {
            this.n = n;
            this.result = 0;
        }
    }

    // Creates a Runnable that calculates the nth Fibonacci number
    public static Runnable fibonacciCalculator(CalculatorBase calc) {
        return () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " computing: fib(" + calc.n + ")");

            long result;
            if (calc.n <= 0) {
                result = 0;
            } else if (calc.n == 1) {
                result = 1;
            } else {
                long prev = 0;
                long curr = 1;
                // Iterative calculation
                for (int i = 2; i <= calc.n; i++) {
                    long next = prev + curr;
                    prev = curr;
                    curr = next;
                    try {
                        // Allow for thread interleaving
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                result = curr;
            }

            calc.result = result;
            System.out.println(threadName + " completed: fibonacci(" + calc.n + ") = " + calc.result);
        };
    }

    // Creates a Runnable that calculates sum of squares from 1 to n
    public static Runnable sumOfSquaresCalculator(CalculatorBase calc) {
        return () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " computing: 1² + 2² + 3² + ... + " + calc.n + "²");

            long sum = 0;
            for (int i = 1; i <= calc.n; i++) {
                sum += (long) i * i;
                try {
                    // Allow for thread interleaving
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            calc.result = sum;
            System.out.println(threadName + " completed: sumOfSquares(" + calc.n + ") = " + calc.result);
        };
    }

    // Main method (PROVIDED - DO NOT MODIFY)
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Threaded Math Calculator ===");
            System.out.println("1. Single calculation demo");
            System.out.println("2. Multiple concurrent calculations");
            System.out.println("3. Exit");
            System.out.print("\nEnter your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    runSingleDemo(scanner);
                    break;
                case 2:
                    runMultipleDemo(scanner);
                    break;
                case 3:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    // Helper method for single calculation (PROVIDED - DO NOT MODIFY)
    private static void runSingleDemo(Scanner scanner) {
        System.out.println("\n=== Single Calculation Demo ===");
        System.out.println("1. Fibonacci");
        System.out.println("2. Sum of Squares");
        System.out.print("\nChoose calculator: ");

        int calcChoice = scanner.nextInt();
        System.out.print("Enter a number (1-20): ");
        int n = scanner.nextInt();

        if (n < 1 || n > 20) {
            System.out.println("Number must be between 1 and 20");
            return;
        }

        CalculatorBase calc = new CalculatorBase(n);
        Runnable task = (calcChoice == 1) ? fibonacciCalculator(calc) : sumOfSquaresCalculator(calc);
        String calcName = (calcChoice == 1) ? "Fibonacci" : "Sum of Squares";

        System.out.println("\nCreating " + calcName + " calculator thread...");
        long startTime = System.currentTimeMillis();

        Thread thread = new Thread(task);
        System.out.println(thread.getName() + " starting calculation for " + n);
        thread.start();

        try {
            System.out.println("\nMain thread waiting for calculation to complete...");
            thread.join();
            System.out.println(thread.getName() + " has finished execution");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\nResult: " + calcName.toLowerCase() + "(" + n + ") = " + calc.result);
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
    }

    // Helper method for multiple calculations (PROVIDED - DO NOT MODIFY)
    private static void runMultipleDemo(Scanner scanner) {
        System.out.println("\n=== Multiple Concurrent Calculations ===");
        System.out.print("Enter a number (1-20): ");
        int n = scanner.nextInt();

        if (n < 1 || n > 20) {
            System.out.println("Number must be between 1 and 20");
            return;
        }

        CalculatorBase fibCalc = new CalculatorBase(n);
        CalculatorBase sumCalc = new CalculatorBase(n);

        Thread fibThread = new Thread(fibonacciCalculator(fibCalc));
        Thread sumThread = new Thread(sumOfSquaresCalculator(sumCalc));

        System.out.println("\nStarting both threads concurrently...\n");
        long startTime = System.currentTimeMillis();

        fibThread.start();
        sumThread.start();

        try {
            System.out.println("\nMain thread waiting for all calculations to complete...");
            fibThread.join();
            sumThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("\n=== Results ===");
        System.out.println("Fibonacci(" + n + ") = " + fibCalc.result);
        System.out.println("Sum of Squares(" + n + ") = " + sumCalc.result);
        System.out.println("\nTotal execution time: " + (endTime - startTime) + "ms");
        System.out.println("All calculations completed successfully!");
    }
}

