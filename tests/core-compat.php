<?php

declare(strict_types=1);

// In-app toast with title and message.
\Pam\Native\System\Toast::message('Mensagem enviada', 'Seu convite foi entregue.', \Pam\Native\System\ToastType::Success, \Pam\Native\System\ToastPosition::Bottom);
// Every legacy class and enum preserves identity when installed.
foreach ([
    'Pam\Native\System\ToastType' => 'Pam\Native\Toast\ToastType',
    'Pam\Native\System\Toast' => 'Pam\Native\Toast\Toast',
    'Pam\Native\System\ToastPosition' => 'Pam\Native\Toast\ToastPosition',
 ] as $legacy => $target) {
    $assert(class_exists($legacy) && is_a($legacy, $target, true), 'Legacy alias identity: '.$legacy);
}

$toastPayload = \Pam\Native\Internal\Wire::decodeMap(TestDiagnostics::$typedCall['payload'] ?? '');
$assert(
    ($toastPayload['title'] ?? null) === 'Mensagem enviada' && ($toastPayload['bottom'] ?? null) === true
        && ($toastPayload['accentColor'] ?? null) === 0xFF69C779,
    'Toast::message must send the in-app toast contract.',
);
\Pam\Native\UI\Icon::register('ToastGlyphs', 'asset://assets/fonts/ToastGlyphs.ttf', ['checkmark-circle' => 0xF0FE]);
\Pam\Native\System\Toast::message(
    'Link copiado',
    titleColor: 0xFFFFFFFF,
    borderColor: 0xFF333333,
    borderWidth: 0.5,
    cornerRadius: 16.0,
    accentWidth: 0.0,
    elevation: 6.0,
    paddingHorizontal: 14.0,
    paddingVertical: 11.0,
    minHeight: 0.0,
    titleLines: 2,
    fitContent: true,
    icon: 'checkmark-circle',
    iconFont: 'ToastGlyphs',
    iconColor: 0xFF00C853,
);
$styledToast = \Pam\Native\Internal\Wire::decodeMap(TestDiagnostics::$typedCall['payload'] ?? '');
$assert(
    ($styledToast['borderColor'] ?? null) === 0xFF333333 && ($styledToast['borderWidth'] ?? null) === 0.5
        && ($styledToast['cornerRadius'] ?? null) === 16.0 && ($styledToast['accentWidth'] ?? null) === 0.0
        && ($styledToast['titleLines'] ?? null) === 2 && ($styledToast['fitContent'] ?? null) === true
        && ($styledToast['iconGlyph'] ?? null) === "\u{F0FE}"
        && ($styledToast['iconFontFamily'] ?? null) === 'asset://assets/fonts/ToastGlyphs.ttf'
        && ($styledToast['iconColor'] ?? null) === 0xFF00C853,
    'Toast::message must send the card style and a font-glyph icon.',
);
\Pam\Native\System\Toast::message('Enviado', icon: 'asset://assets/icons/check.png', borderColor: 0xFF000000);
$assetToast = \Pam\Native\Internal\Wire::decodeMap(TestDiagnostics::$typedCall['payload'] ?? '');
$assert(
    ($assetToast['iconSource'] ?? null) === 'asset://assets/icons/check.png' && !isset($assetToast['iconGlyph'])
        && !isset($assetToast['borderColor']) && ($assetToast['accentWidth'] ?? null) === 5.0,
    'Toast::message must send a packaged PNG icon by reference and skip a zero-width border.',
);
\Pam\Native\System\Toast::message('Sem ícone', icon: 'missing-glyph', iconFont: 'ToastGlyphs');
$assert(
    !isset(\Pam\Native\Internal\Wire::decodeMap(TestDiagnostics::$typedCall['payload'] ?? '')['iconSize']),
    'An unknown toast glyph draws the toast without an icon.',
);

