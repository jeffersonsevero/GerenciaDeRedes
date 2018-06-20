import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClienteOficial {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.println("Insira o ip do servidor: ");
        String ip = in.next();

        System.out.println("Insira porta");
        int porta = in.nextInt();

        try (Socket conexao = new Socket(ip, porta)) {

            Scanner scan = new Scanner(System.in);




            DataInputStream entrada = new DataInputStream(conexao.getInputStream());
            DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());

            String mensagem = entrada.readUTF();
            System.out.println(mensagem);
            String resposta = scan.next();

            saida.writeUTF(resposta);

            while (true){
                String opcao = entrada.readUTF();

                if(opcao.equals("D")) {
                    escolherOpcoes(conexao);
                    conexao.close();
                    break;
                }
                if(opcao.equals("U")) {

                    upload(conexao);
                    conexao.close();
                    break;

            }


            }
        } catch (Exception e) {
            System.out.println("Erro ao criar socket cliente");
            e.printStackTrace();
        }


    }


    public static void escolherOpcoes(Socket conexao) throws Exception {

        Scanner in = new Scanner(System.in);

        DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexao.getInputStream());

        String mensagemOpcoes = entrada.readUTF();
        System.out.println(mensagemOpcoes);

        //Enviar opção para servidor
        String opcao = in.next();
        //Enviando opção de sim ou não para servidor
        saida.writeUTF(opcao);

        while (true) {
            //Recebendo mensagem login
            System.out.println(entrada.readUTF());
            String login = in.next();
            //Mandando o login
            saida.writeUTF(login);

            System.out.println(entrada.readUTF());
            String senha = in.next();
            saida.writeUTF(senha);

            String mensagem = entrada.readUTF();
            System.out.println(entrada.readUTF());


            if (mensagem.equals("Entrando no servidor de arquivos")) {

                arquivos(conexao);
                break;

            } else if (mensagem.equals("Esse login já existe, digite outro!")) {
                System.out.println(mensagem);


            }


        }


    }


    public static void arquivos(Socket conexao) {

        DataInputStream entrada = null;
        try {
            entrada = new DataInputStream(conexao.getInputStream());
        } catch (IOException e) {
            System.out.println("Erro ao criar entrada de dados");
        }

        BufferedWriter saida = null;
        try {
            saida = new BufferedWriter(new OutputStreamWriter(conexao.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Erro ao criar saida de dados");
        }

        while (true) {

            String linha = null;
            try {
                linha = entrada.readUTF();
            } catch (IOException e) {
                System.out.println("Erro ao receber informações do servidor");
            }
            System.out.println(linha);
            if (linha.equals(".....")) break;


        }
        Scanner scan = new Scanner(System.in);


        String arquivo = scan.next();

        try {
            saida.write(arquivo);
        } catch (IOException e) {
            System.out.println("Erro ao escrever nome de arquivo");
        }

        try {
            saida.newLine();
        } catch (IOException e) {
            System.out.println("Erro em newLine");
        }

        try {
            saida.flush();
        } catch (IOException e) {
            System.out.println("Erro no flush");
        }

        String linha = null;
        try {
            linha = entrada.readUTF();
        } catch (IOException e) {
            System.out.println("Erro ao ler linha");
        }
        System.out.println(linha);

        DataOutputStream saidaParaArquivo = null;
        try {
            saidaParaArquivo = new DataOutputStream(new FileOutputStream("copy_" + arquivo));
        } catch (FileNotFoundException e) {
            System.out.println("Erro ao fazer arquivo local para cópia");
        }

        byte[] array = new byte[4096];

        while (true) {

            int resultado = 0;
            try {
                resultado = entrada.read(array, 0, 4096);
            } catch (IOException e) {
                System.out.println("Erro ao formular array de bytes");
            }
            if (resultado == -1) break;

            try {
                saidaParaArquivo.write(array, 0, resultado);
            } catch (IOException e) {
                System.out.println("Erro ao mandar arquivo ao receber arquivo");
            }


        }

        try {
            saida.close();
            entrada.close();
            conexao.close();
        } catch (Exception e) {
            System.out.println("Erro ao fechar conexões");
        }


    }


    public static void upload(Socket conexao) throws Exception {

        Scanner in = new Scanner(System.in);

        DataInputStream entrada = new DataInputStream(conexao.getInputStream());
        ObjectOutputStream saida = new ObjectOutputStream(conexao.getOutputStream());


        String mensagem = entrada.readUTF();

        System.out.println(entrada);

        String caminho = in.next();
        saida.writeUTF(caminho);



        FileInputStream arquivo = new FileInputStream("/home/jeff/Documentos/" + caminho);

        byte[] buf = new byte[4096];

        while (true){

            int tamanho = arquivo.read(buf);

            if(tamanho ==  -1) break;
            saida.write(buf,0, tamanho);
        }







    }

    public static void uploda(Socket cliente) throws Exception{



        ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());

        DataInputStream entrada = new DataInputStream(cliente.getInputStream());
        DataOutputStream out = new DataOutputStream(cliente.getOutputStream());

        Scanner in = new Scanner(System.in);


        System.out.println(entrada.readUTF());
        String arq = in.next();

        //Enviar nome de arquivo
        out.writeUTF(arq);


        FileInputStream arquivo = new FileInputStream("/home/jeff/Documentos/" + arq);

        byte[] buf = new byte[4096];

        while (true){

            int tamanho = arquivo.read(buf);

            if(tamanho ==  -1) break;
            saida.write(buf,0, tamanho);
        }


    }

}
