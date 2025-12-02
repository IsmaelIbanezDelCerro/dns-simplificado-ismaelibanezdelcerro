import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.ArrayList;

public class ServidorDNS {
    public static void main(String[] args) throws IOException {
        HashMap<String, ArrayList<Registro>> registros = new HashMap<>();
        int puerto = 5000;

        try (BufferedReader br = new BufferedReader(new FileReader("src/recursos.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(" ");

                String dominio = partes[0];
                String tipo = partes[1];
                String ip = partes[2];

                registros.put(dominio, new ArrayList<>());
                registros.get(dominio).add(new Registro(dominio, tipo, ip));
            }

            System.out.println("Diccionario cargado correctamente.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        while(true) {
            try (ServerSocket serverSocket = new ServerSocket(puerto)) {
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado.");

                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {
                    try {
                        // EXIT
                        if (mensaje.equalsIgnoreCase("EXIT")) {
                            salida.println("Hasta luego");
                            break;
                        }

                        if (!mensaje.startsWith("LOOKUP ")) {
                            salida.println("400 Bad request");
                            continue;
                        }

                        // LOOKUP <tipo> <dominio>
                        String[] partes = mensaje.split(" ");
                        if (partes.length != 3) {
                            salida.println("400 Bad request");
                            continue;
                        }

                        String tipo = partes[1];
                        String dominio = partes[2];

                        // Dominio no existe
                        if (!registros.containsKey(dominio)) {
                            salida.println("404 Not Found");
                            continue;
                        }

                        // Buscar el tipo A
                        boolean encontrado = false;
                        for (Registro r : registros.get(dominio)) {
                            if (r.getTipo().equalsIgnoreCase(tipo)) {
                                salida.println("200 " + r.getIp());
                                encontrado = true;
                            }
                        }

                        if (!encontrado) {
                            salida.println("404 Not Found");
                        }

                    } catch (Exception e) {
                        salida.println("500 Server error");
                    }
                }
                System.out.println("Cliente desconectado.");
                socket.close();

            }
        }
    }
}

