package triangulo;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		int ladoA,ladoB,ladoC;
		String tipoTriangulo;
		Scanner scanner = new Scanner(System.in);
		
		try {
			System.out.println("Ingrese el valor del primer lado: ");
			ladoA = scanner.nextInt();
			
			System.out.println("Ingrese el valor del segundo lado: ");
			ladoB = scanner.nextInt();
			
			System.out.println("Ingrese el valor del tercer lado: ");
			ladoC = scanner.nextInt();
			
		}catch(InputMismatchException e) {
			System.out.println("El tipo de dato ingresado es incorrecto. Ingrese valores enteros");
			return;
			
		}finally {
			scanner.close();
		}

		
		Triangulo triangulo = null;
		try {
			triangulo = new Triangulo(ladoA, ladoB, ladoC);			
		}catch(TrianguloException e) {
			System.out.println(e.getMessage());
			return;
		}
		
		tipoTriangulo = triangulo.tipoTriangulo();
		
		System.out.println("El triangulo es " + tipoTriangulo);
	}
	
}
