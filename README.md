# Jogo da Velha

Jogo da velha em modo texto, organizado no padrão MVC.

## Arquitetura

- `model`: dados da partida (`State`) e arquivo `game.json` (`Repository`).
- `view`: desenho do tabuleiro (`BoardRenderer`) e leitura do teclado (`GameView`).
- `controller`: regras (`GameController`) — cursor, jogada, troca de jogador, vitória, empate e reinício.
- `Main.kt` só liga as três partes.

## Teclas

- Setas: movem o cursor. Na casa vazia, o símbolo da vez aparece apagado dentro de `[ ]`.
- Espaço: confirma a jogada.
- `R`: reinicia.
- `S`: sai.

Quando alguém vence, só as três casas da linha vencedora ficam em negrito (X em ciano, O em amarelo). O resto do tabuleiro permanece na cor do terminal.

## Como executar

Rode a função `main` de `Main.kt` pelo IntelliJ. Em Run > Edit Configurations, adicione em VM options:

```text
--enable-native-access=ALL-UNNAMED
```

## Arquivo da partida

A partida é salva automaticamente em `game.json`, na pasta de execução:

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

`null` é casa vazia. Se o arquivo não existir ou estiver inválido, começa um jogo novo. Ao carregar, a vitória é calculada de novo a partir do tabuleiro.
