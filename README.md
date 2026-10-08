# Jogo da Velha

Jogo da velha em modo texto, organizado no padrão MVC.

## Arquitetura

- `model`: partida (`State`, `Player`, `Status`) e o arquivo `game.json` (`Repository`).
- `view`: terminal (`Screen`), desenho do tabuleiro (`Board`), cursor (`Cursor`), teclado (`Keyboard`) e comandos (`Command`). As cores ANSI ficam em `utils.Color`.
- `controller`: fluxo da partida (`Match`) e regras (`Rules`) — jogada, troca de jogador, vitória e empate.
- `Main.kt` só liga as três partes: `Match(Screen(), Repository()).run()`.

`Screen` abre o terminal em modo raw, numa tela alternativa. As setas só movem o cursor enquanto a partida está em andamento; os demais comandos voltam para `Match`.

## Teclas

- Setas: movem o cursor no tabuleiro 3×3. Na casa vazia, o símbolo da vez aparece apagado dentro de `[ ]`. Na casa ocupada, a marca fica em negrito e os colchetes ficam vermelhos.
- Espaço: confirma a jogada na casa do cursor. Casa ocupada ou partida já encerrada não altera o tabuleiro.
- `R`: pede confirmação e reinicia (X começa, cursor no canto).
- `S`: pede confirmação e sai.

As marcas ficam em negrito. Quando alguém vence, só as três casas da linha vencedora ficam em negrito verde. No empate, todas ficam em negrito amarelo. Em vitória ou empate, o cursor some e a ajuda passa a mostrar só `R` e `S`. As perguntas de confirmação aparecem em magenta.

## Exemplo

Partida em andamento, vez de O, cursor em c2. As cores do terminal não aparecem aqui: o `O` entre colchetes fica apagado.

```text
Vez do Jogador: [ O ]

       a   b   c

  1      |   | X 
      ---|---|---
  2      | O |[O]
      ---|---|---
  3    X |   |   


Setas: mover | Espaço: jogar | R: reiniciar | S: sair
```

## Como executar

Pelo Gradle, na pasta do projeto:

```text
./gradlew run
```

O plugin de aplicação já define a classe `MainKt` e a opção de JVM `--enable-native-access=ALL-UNNAMED`, exigida pelo JLine.

Pelo IntelliJ, rode a função `main` de `Main.kt`. Em Run > Edit Configurations, adicione em VM options:

```text
--enable-native-access=ALL-UNNAMED
```

## Arquivo da partida

A partida em andamento, com ao menos uma jogada, é salva em `game.json`, na pasta de execução, depois de cada jogada. Se o terminal fechar no meio da partida, o último save permanece. Vitória, empate, reinício e tabuleiro vazio apagam o arquivo.

Na abertura, se existir um jogo em andamento, o programa pergunta se deseja restaurar. `S` continua essa partida; `N` apaga o arquivo e começa outra. Um save já encerrado é apagado sem pergunta.

```json
{
  "currentPlayer": "X",
  "status": "IN_PROGRESS",
  "board": [
    [null, null, "X"],
    [null, "O", null],
    ["X", null, null]
  ]
}
```

`null` é casa vazia. `status` pode ser `IN_PROGRESS`, `WIN` ou `DRAW`. Se o arquivo não existir, estiver inválido ou o tabuleiro não for 3×3, começa um jogo novo.
