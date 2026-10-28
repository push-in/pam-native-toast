import Foundation
import UIKit
import PamNative

public final class ToastModule: NativeModule, ClosableNativeModule, @unchecked Sendable {
    private var closed = false
    private var backgroundObserver: NSObjectProtocol?
    public init() {
        backgroundObserver = NotificationCenter.default.addObserver(forName: UIApplication.didEnterBackgroundNotification, object: nil, queue: .main) { _ in
            PamToastPresenter.dismiss()
        }
    }
    public func invoke(method: String, payload: Data, completion: @escaping ModuleCompletion) {
        guard method == "show" else { completion(.failure, Data("Unknown toast method".utf8)); return }
        do {
            let spec = PamToastSpec.decode(try WireMap.decode(payload))
            DispatchQueue.main.async {
                guard !self.closed else { completion(.failure, Data("Toast host closed".utf8)); return }
                if UIApplication.shared.applicationState == .active { PamToastPresenter.show(spec) }
                completion(.success, Data())
            }
        } catch { completion(.failure, Data(error.localizedDescription.utf8)) }
    }
    public func close() {
        if let backgroundObserver { NotificationCenter.default.removeObserver(backgroundObserver) }
        backgroundObserver = nil
        DispatchQueue.main.async { self.closed = true; PamToastPresenter.dismiss() }
    }
    deinit { if let backgroundObserver { NotificationCenter.default.removeObserver(backgroundObserver) } }
}
