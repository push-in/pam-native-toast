// swift-tools-version: 5.9
import PackageDescription
import Foundation

let core = ProcessInfo.processInfo.environment["PAM_NATIVE_CORE"] ?? "../../pam-native"
let package = Package(
    name: "PamNativeToast",
    platforms: [.iOS(.v15)],
    products: [.library(name: "PamNativeToast", targets: ["PamNativeToast"])],
    dependencies: [.package(path: core + "/ios")],
    targets: [
        .target(name: "PamNativeToast", dependencies: [.product(name: "PamNative", package: "ios")], path: "Sources"),
        .testTarget(name: "PamNativeToastTests", dependencies: ["PamNativeToast", .product(name: "PamNative", package: "ios")], path: "Tests"),
    ]
)
