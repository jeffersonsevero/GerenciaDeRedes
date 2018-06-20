 import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorOficial {

    public static void main(String[] args) {


        ServerSocket server = null;

        try {

            System.out.println("Criando servidor...");
            server = new ServerSocket(8888);
        } catch (Exception e) {

        }

        System.out.println("Iniciando servidor...");
        while (true) {

            try {

                System.out.println("Esperando nova conexão");

                Socket cliente = server.accept();

                DataInputStream entrada = new DataInputStream(cliente.getInputStream());
                DataOutputStream saida = new DataOutputStream(cliente.getOutputStream());

                saida.writeUTF("Download ou Upload? ");

                String resposta = entrada.readUTF();
                System.out.println(resposta);

                if(resposta.equals("Download")) {
                    saida.writeUTF("D");

                    escolherOpcoes(cliente);
                    cliente.close();
                    break;
                }else if(resposta.equals("Upload")){
                    saida.writeUTF("U");
                    upload(cliente);
                    cliente.close();
                    break;
                }


            } catch (Exception e) {
                System.out.println("Erro ao conectar com cliente");
            }


        }




    }

    public static void escolherOpcoes(Socket conexao) throws Exception{

        DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexao.getInputStream());

        try {
            saida.writeUTF("========================== Escolha a opção ============================== \n" +
                    "1 - Entrar \n2 - Cadastrar ");
        } catch (IOException e) {
            System.out.println("Erro ao enviar mesagem de opções");
            e.printStackTrace();
        }

        String opcao = entrada.readUTF();

        if(opcao.equals("1")){

            while (true){

                saida.writeUTF("Login: ");
                //Capturando o login
                String login = entrada.readUTF();
                System.out.println(login);

                saida.writeUTF("Senha: ");
                String senha = entrada.readUTF();
                System.out.println(senha);




                System.out.println("Lendo logins existentes...");
                FileReader ler = new FileReader("clientes.txt");
                BufferedReader reader = new BufferedReader(ler);

                String linha;

                System.out.println("Outros logins encontrados. Verificando se o login '" + login + "' já existe");
                boolean loginJaExiste = false;

                while((linha = reader.readLine()) != null ){
                    String LoginESenha = login + ":" + senha;

                    if((linha).equals(LoginESenha)){
                        loginJaExiste = true;
                        saida.writeUTF("Entrando no servidor de arquivos");
                        arquivos(conexao);
                        break;



                    }else{
                        System.out.println("Informações erradas");

                    }




                }


            }





        }

        else if(opcao.equals("2")){

            while (true){

                saida.writeUTF("Insira login a ser cadastrado: ");
                String login = entrada.readUTF();
                System.out.println(login);

                saida.writeUTF("Insira senha a ser cadastrada: ");
                String senha = entrada.readUTF();
                System.out.println(senha);


                File arquivo = new File("clientes.txt");
                if (!arquivo.exists()) {

                    System.out.println("Criando arquivo 'clients.txt'");
                    arquivo.createNewFile();

                }

                System.out.println("Lendo logins existentes...");
                FileReader ler = new FileReader("clientes.txt");
                BufferedReader reader = new BufferedReader(ler);

                String linha;

                System.out.println("Outros logins encontrados. Verificando se o login '" + login + "' já existe");
                boolean loginJaExiste = false;


                while ((linha = reader.readLine()) != null) {

                    String existingLogin = obterLogin(linha);

                    if (login.equals(existingLogin)) {

                        loginJaExiste = true;
                        break;

                    }

                    if (loginJaExiste) {

                        saida.writeUTF("Esse login já existe, digite outro!");


                    }

                }




            }









        }




    }







    public static String obterSenha(String nome){

        int i = nome.indexOf(":");

        String apenasSenha = nome.substring(i);

        return apenasSenha;
    }

    private static String obterLogin(String line) {
        int i = line.indexOf(":");
        String login = line.substring(0, i);
        return login;
    }


    public  static void arquivos(Socket conexao) throws Exception{

        //Cria stream para receber comandos do cliente
        BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
        //Cria stream para enviar texto e dados
        DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());

        //Mandamos uma mensagem
        try {
            saida.writeUTF("Bem vindo ao servidor de arquivos");
            saida.writeUTF("=================================");
        }catch (Exception e){
            System.out.println("Erro ao enviar mensagem de Bem vindo");
        }

        try {
            //Enviamos ao cliente a lista de arquivos
            File diretorio = new File("/home/jeff/Documentos/ArquivosCompartilhados");
            String[] arquivos = diretorio.list();
            for (int i = 0; i < arquivos.length; i++) {
                saida.writeUTF(arquivos[i]);
            }
        }catch (Exception e){
            System.out.println("Erro ao listar arquivos para cliente");
        }


        //Aguardamos a seleção do usuário
        try {
            saida.writeUTF("-----------------------------------");
            saida.writeUTF("Selecione um dos arquivos acima");
            saida.writeUTF(".....");
            saida.flush();
        }catch (Exception e){
            System.out.println("Erro ao enviar inforamações prévias antes da escolha do arquivo");
        }



        //Lemos o nome selecionado pelo cliente
        String nomeSelecionado = "/home/jeff/Documentos/ArquivosCompartilhados/" + entrada.readLine();

        //Criando representação do arquivo
        File selecionado = new File(nomeSelecionado);

        //Enviando mensagem esclarecedora
        try {
            saida.writeUTF("Enviando arquivo ");

            saida.flush();
        }catch (Exception e){
            System.out.println("Erro ao enviar mensagem  Enviando arquivo");
        }

        //Abrir arquivo localmente

        DataInputStream entradaLocal = new DataInputStream(new FileInputStream(selecionado));


        //Ler todos os bytes do arquivo local enviando para cliente em blocos de 25 bytes
        byte [] buf = new byte[4096];

        while(true) {
            try {
                //Tentar ler até 25 bytes do arquivo de entrada
                int resultado = entradaLocal.read(buf, 0, 4096);
                if (resultado == -1) break;

                //Escrevemos somente bytes lidos
                saida.write(buf, 0, resultado);
            }catch (Exception e){
                System.out.println("Erro ao criar e transferir bytes para cliente");
            }

        }

        //fechamos conexões
        try {
            entradaLocal.close();
            saida.close();
            conexao.close();
        }catch (Exception e){
            System.out.println("Erro ao fechar conexões do servidor");
        }

    }



    public static void upload(Socket conexao) throws Exception{

        ObjectInputStream entrada = new ObjectInputStream(conexao.getInputStream());

        DataOutputStream saida =  new DataOutputStream(conexao.getOutputStream());
        DataInputStream in = new DataInputStream(conexao.getInputStream());

        saida.writeUTF("Deseja mandar qual arquivo para o servidor: ");

        String arq = in.readUTF();

        //Arquivo vindo do cliente
        FileOutputStream arquivo = new FileOutputStream("/home/jeff/" + arq );

        byte[] buf = new byte[4096];

        while (true){

            int tamanho = entrada.read(buf);

            if(tamanho ==  -1) break;
            arquivo.write(buf,0, tamanho);
        }

    }










}
