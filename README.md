# PAM Native Toast

Plugin opcional `pushinbr/pam-native-toast` para PHP 8.5 e PAM Native 1.46.
Código Apache-2.0. Android API 26+ e iOS 15+.

```sh
composer require pushinbr/pam-native-toast:0.1.0
```

O Composer e o launcher PAM descobrem `pam-native.plugin.json`, registram o
provider PHP e compilam as fontes nativas. Não há instalação manual de módulo.
É necessário reconstruir o host nativo após adicionar o pacote.

## Uso

```php
use Pam\Native\Toast\{Toast, ToastPosition, ToastType};

Toast::show('Salvo');
Toast::message('Salvo', 'Alterações concluídas.',
    type: ToastType::Success,
    position: ToastPosition::Bottom,
    durationMs: 2200,
    offset: 140,
);
```

Cores ARGB, fontes, ícones, borda, raio, dimensões e variantes mantêm os mesmos
parâmetros da API anterior. `ToastType` e `ToastPosition` são enums inteiros
sequenciais a partir de 1. Os valores visuais anteriores são preservados.

A operação histórica `NativeOperation::Toast` é apenas encaminhada pelo núcleo
para `toast.show`, no registro genérico de módulos. Também é possível chamar
`NativeModules::call('toast', 'show', $values, $callback)` diretamente.
Sem este plugin o host não inclui renderizador de Toast.

Há no máximo um toast visível. Substituição, fechamento do host e background
cancelam a apresentação e os timers. Ícones são decodificados em tamanho limitado;
fontes têm cache limitado. A posição usa safe area e acompanha o teclado.

## Migração e compatibilidade

Os nomes antigos do núcleo são shims `MovedToPlugin` por uma versão menor.
Com o plugin, tornam-se aliases e emitem depreciação; sem ele, o erro informa
`composer require pushinbr/pam-native-toast`. Migre imports para
`Pam\Native\Toast`. Nenhuma dependência deste plugin é imposta a outros apps.

## Testes

As regressões PHP foram movidas para `tests/core-compat.php`. Para executá-las,
use o PHP 8.5 no checkout do framework:

```sh
PAM_TEST_PLUGIN_ROOTS=/caminho/para/este-plugin php packages/native/tests/run.php
```

Os testes Android estão em `android/src/test` ou `android/src/androidTest`,
conforme o pacote. Os testes instrumentados precisam de um host de teste PAM.
O pacote de testes `ios/Package.swift` aceita `PAM_NATIVE_CORE=/checkout/core`.
Execute os testes Swift em um simulador iOS; o módulo de teste é independente
do nome de módulo que o launcher gera no host de produção.

A validação em emulador deve incluir tema claro/escuro, teclado e safe areas,
movimento reduzido, substituição e fechamento da tela. Consulte `RELATORIO.md`
para a evidência efetivamente executada nesta versão.
