package libreria;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.ConsoleHandler;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class MainLibreria {
//Definicion de Variables y recursos varios
	public static Scanner teclado = new Scanner(System.in);
    public static PrintStream out = System.out;
    public static String ruta = "libros.tsv";   
//Main
    public static void main(String[] args) {

        Funcion<Libro> imprimir = new Funcion<Libro>() {
            @Override
            public void funcion(Libro libro, Object parametros) {
                out.println(libro);
                int[] contador = (int[]) parametros;
                contador[0]++;
            }
        };
        Funcion<Libro> imprimirEnArchivo = new Funcion<Libro>() {
            @Override
            public void funcion(Libro libro, Object parametros) {
                PrintStream archivo = (PrintStream) parametros;
                archivo.print(libro.getISBN() + "\t");
                archivo.print(libro.getTitulo() + "\t");
                archivo.print(libro.getAutor() + "\t");
                archivo.print(libro.getEditorial() + "\t");
                archivo.print(libro.getEdicion() + "\t");
                archivo.print(libro.getAnno_de_publicacion() + "\n");
            }
        };
        Vector<Libro> vector = lecturaArchivo();
        int i, n;
        Libro dato = null, libro;
        int[] contador = {0};
        int opcion, subopcion;
        login();
        do {
        	libro = new Libro();
        	mostrarMenuPrincipal();
            opcion=obtenerOpcion();
            out.println();
            if ( verificarExistenciaLibro(vector, opcion)) {
                pausar("No hay registros.\n");
                continue;
            }
            
            if (opcion<5) {
                libro.setISBN(leer_cadena ("Ingrese el ISBN del libro"));
                i = vector.indexOf(libro);
                dato = i<0 ? null : vector.get(i);
                if (dato!=null) {
                    out.println();
                    imprimir.funcion(dato, contador);
                }
            }
            
            if (verificarExistenciaRegistro(opcion, dato)) {
            	out.println("El registro ya existe.");
            }    
            else if (verificarInexistenciaRegistro(opcion, dato)) {
            	out.println("\nRegistro no encontrado.");
            }
            else switch (opcion) {
            case 1:
            	darDeAlta(libro, vector);
                break;
            case 3:
                mostrarMenuUpdate();
                actualizar(libro,vector);
                break;
            case 4:
                eliminarRegistro(vector, dato);
                break;
            case 5:
                ordenarRegistros(vector);
                break;
            case 6:
                n = vector.size();
                contador[0] = 0;
                for (i=0; i<n; i++)
                    imprimir.funcion(vector.get(i), contador);
                out.println("Total de registros: " + contador[0] + ".");
                break;
            }
            if (verificarOpcion(opcion))
                pausar("");
        } while (opcion!=7);
        try {
            PrintStream salida = new PrintStream(ruta);
            n = vector.size();
            for (i=0; i<n; i++)
                imprimirEnArchivo.funcion(vector.get(i), salida);
            salida.close();
        } catch (FileNotFoundException e) {}
    }

//Metodos auxiliares
    public static void pausar(String mensage) {
        out.print(mensage + "\nPresione <ENTER> para continuar . . . ");
        teclado.nextLine();
        out.println();
    }

    public static String leer_cadena(String mensaje) {
        out.print(mensaje + ": ");
        return teclado.nextLine();
    }

    public static int leer_entero(String mensaje) {
        try {
            return Integer.parseInt(leer_cadena(mensaje));
        } catch (NumberFormatException e) {
            out.print("N\u00FAmero incorrecto.");
            return leer_entero(mensaje);
        }
    }
    
    public static Vector<Libro>  lecturaArchivo() {
    	if(!System.getProperties().get("os.name").equals("Linux") && System.console()!=null)
            try {
                out = new PrintStream(System.out, true, "CP850");
                teclado = new Scanner(System.in, "CP850");
            } catch (UnsupportedEncodingException e) {}
        Vector<Libro> vector = new Vector<Libro>();
        int i, n;
        Libro dato = null, libro;
        int[] contador = {0};
        int opcion, subopcion;
        String[] campos;
        try {
            Scanner entrada = new Scanner(new FileReader(ruta));
            while (entrada.hasNextLine()) {
                campos = entrada.nextLine().split("\t");
                libro = new Libro();
                libro.setISBN(campos[0]);
                libro.setTitulo(campos[1]);
                libro.setAutor(campos[2]);
                libro.setEditorial(campos[3]);
                libro.setEdicion(Integer.parseInt(campos[4]));
                libro.setAnno_de_publicacion(Integer.parseInt(campos[5]));
                vector.add(libro);
            }
            entrada.close();
        } catch (FileNotFoundException e) {} //Lectura
    	return vector;
    }
    
    public static void mostrarMenuPrincipal() {
    	out.println("MEN\u00DA");
        out.println("1.- Altas");
        out.println("2.- Consultas");
        out.println("3.- Actualizaciones");
        out.println("4.- Bajas");
        out.println("5.- Ordenar registros");
        out.println("6.- Listar registros");
        out.println("7.- Salir");
    }
    
    public static void mostrarMenuUpdate() {
    	out.println("Men\u00FA de modificaci\u00F3n de campos");
        out.println("1.- titulo");
        out.println("2.- autor");
        out.println("3.- editorial");
        out.println("4.- edicion");
        out.println("5.- anno de publicacion");
    }
    
    public static void darDeAlta(Libro libro, Vector<Libro> vector) {
    	libro.setTitulo(leer_cadena ("Ingrese el titulo"));
        libro.setAutor(leer_cadena ("Ingrese el autor"));
        libro.setEditorial(leer_cadena ("Ingrese el editorial"));
        libro.setEdicion(leer_entero ("Ingrese el edicion"));
        libro.setAnno_de_publicacion(leer_entero ("Ingrese el anno de publicacion"));
        vector.add(libro);
        libro = new Libro();
        out.println("\nRegistro agregado correctamente.");
    }
    
    public static void actualizar(Libro dato, Vector<Libro> vector) {
    	int subopcion, i;
    	i=vector.indexOf(dato);
    	vector.remove(i);
    	do {
            subopcion = leer_entero ("Seleccione un n\u00FAmero de campo a modificar");
            if (subopcion<1 || subopcion>5)
                out.println("Opci\u00F3n no v\u00E1lida.");
        } while (subopcion<1 || subopcion>5);
        switch (subopcion) {
            case 1:
                dato.setTitulo(leer_cadena ("Ingrese el nuevo titulo"));
                break;
            case 2:
                dato.setAutor(leer_cadena ("Ingrese el nuevo autor"));
                break;
            case 3:
                dato.setEditorial(leer_cadena ("Ingrese el nuevo editorial"));
                break;
            case 4:
                dato.setEdicion(leer_entero ("Ingrese el nuevo edicion"));
                break;
            case 5:
                dato.setAnno_de_publicacion(leer_entero ("Ingrese el nuevo anno de publicacion"));
                break;
        }
        //vector.remove(dato);
        vector.add(dato);
        out.println("\nRegistro actualizado correctamente.");
    }
    
    public static void eliminarRegistro(Vector<Libro> vector, Libro dato) {
    	vector.remove(dato);
        out.println("Registro borrado correctamente.");
    }
    
    public static void ordenarRegistros(Vector<Libro> vector) {
    	Collections.sort(vector);
        out.println("Registros ordenados correctamente.");
    }
    
    public static int obtenerOpcion() {
    	int opcion;
    	do {
            opcion = leer_entero ("Seleccione una opci\u00F3n");
            if(opcion<1 || opcion>7)
                out.println("Opci\u00F3nn no v\u00E1lida.");
        } while (opcion<1 || opcion>7);
    	return opcion;
    }
    
    public static boolean verificarExistenciaLibro(Vector<Libro> vector, int opcion) {
    	return (vector.isEmpty() && opcion!=1 && opcion!=7);
    }
    
    public static boolean verificarExistenciaRegistro(int opcion, Libro dato) {
    	return (opcion==1 && dato!=null);
    }
    
    public static boolean verificarInexistenciaRegistro(int opcion, Libro dato) {
    	return (opcion>=2 && opcion<=4 && dato==null);
    }
    
    public static boolean verificarOpcion(int opcion) {
    	return (opcion<7 && opcion>=1);
    }
    
    public static void login() {
    	String pass;
    	final JPasswordField pf = new JPasswordField(); 
    	do {
        	pass = JOptionPane.showConfirmDialog( null, pf, "Enter password : ",JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE ) == JOptionPane.OK_OPTION ? new String( pf.getPassword() ) : "";
    	}while(!pass.toString().equalsIgnoreCase("Ms123456"));
    	
    }
}
