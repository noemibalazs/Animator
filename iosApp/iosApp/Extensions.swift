import SwiftUI
import ComposeApp

extension Data {
    func toKotlinByteArray() -> KotlinByteArray {
        KotlinByteArray(size: Int32(self.count)) { index in
            let byte = self[Int(truncating: index)]
            return KotlinByte(value: Int8(bitPattern: byte))
        }
    }
}
