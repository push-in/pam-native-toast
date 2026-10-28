import UIKit
import XCTest
import PamNative
@testable import PamNativeToast

@MainActor
final class PamToastTests: XCTestCase {
    func testToastMessageDecodesReactNativeToastStyling() {
        let spec = PamToastSpec.decode([
            "title": .text("Saved"),
            "message": .text("Your profile was updated"),
            "bottom": .flag(true),
            "durationMs": .integer(2_500),
            "offset": .decimal(60),
            "accentColor": .integer(0xFF00_AA00),
            "titleSize": .decimal(14),
        ])
        XCTAssertFalse(spec.plain)
        XCTAssertEqual(spec.title, "Saved")
        XCTAssertTrue(spec.bottom)
        XCTAssertEqual(spec.durationMs, 2_500)
        XCTAssertEqual(spec.offset, 60)
        XCTAssertEqual(spec.accentColor, 0xFF00_AA00)
        XCTAssertEqual(spec.titleSize, 14)
        XCTAssertEqual(spec.messageSize, 10)
        let plain = PamToastSpec.decode(["message": .text("Copied"), "long": .flag(true)])
        XCTAssertTrue(plain.plain)
        XCTAssertEqual(plain.durationMs, 3_500)
    }

    func testToastMessageDecodesCardStyleAndIcon() {
        let spec = PamToastSpec.decode([
            "title": .text("Link copiado"),
            "borderColor": .integer(0xFF33_3333),
            "borderWidth": .decimal(0.5),
            "cornerRadius": .decimal(16),
            "accentWidth": .decimal(0),
            "elevation": .decimal(6),
            "titleLines": .integer(2),
            "fitContent": .flag(true),
            "minHeight": .decimal(0),
            "iconGlyph": .text("\u{F2B4}"),
            "iconFontFamily": .text("asset://assets/fonts/Ionicons.ttf"),
            "iconColor": .integer(0xFF00_C853),
            "iconSize": .decimal(18),
        ])
        XCTAssertEqual(spec.borderColor, 0xFF33_3333)
        XCTAssertEqual(spec.borderWidth, 0.5)
        XCTAssertEqual(spec.cornerRadius, 16)
        XCTAssertEqual(spec.accentWidth, 0)
        XCTAssertEqual(spec.titleLines, 2)
        XCTAssertTrue(spec.fitContent)
        XCTAssertEqual(spec.minHeight, 0)
        XCTAssertEqual(spec.iconGlyph, "\u{F2B4}")
        XCTAssertEqual(spec.iconColor, 0xFF00_C853)
        let defaults = PamToastSpec.decode(["title": .text("Saved")])
        XCTAssertEqual(defaults.accentWidth, 5)
        XCTAssertEqual(defaults.cornerRadius, 6)
        XCTAssertEqual(defaults.titleLines, 1)
        XCTAssertNil(defaults.borderColor)
        XCTAssertNil(defaults.iconGlyph)
    }

}
