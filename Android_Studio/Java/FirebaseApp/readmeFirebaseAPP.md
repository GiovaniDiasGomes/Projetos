## Nome do Projeto - Firebase APP

Esse projeto visa apresentar a utilização do Firebase, um modelo de serviço de backend
que busca prover infraestrutura e o backend (autenticação de usuário, armazenamento, escalabilidade,
 serviço de notificações, entre outros) para um APP.

## Tecnologias 
 
Aqui estão as tecnologias usadas no projeto.
 
* Android Build versão  3.5.2
* SDK versão  32.0.0
* Firebase Database versão 16.0.3
* Firebase Storage versão  16.1.0
* Firebase UI Storage versão  4.3.1
* Glide versão  4.13.0
 
## Serviços usados
 
* Github
  
## Como usar
 
Na tela principal do app, temos a visualização de uma imagem e um botão, no qual ao ser clicado 
faz uma requisição para o Firebase, buscando alterar a imagem do APP.  
No Firebase, a imagem será requisitada no Storage e enviará o arquivo por meio da função Glide
que atualizará a imagem do aplicativo.

## Links

Tela principal - app/src/main/res/layout/activity_main.xml  
Código - app/src/main/java/com/example/firebaseapp/MainActivity.java  
 
## Autor
 
* Giovani Dias Gomes: @GiovaniDiasGomes (https://github.com/GiovaniDiasGomes)