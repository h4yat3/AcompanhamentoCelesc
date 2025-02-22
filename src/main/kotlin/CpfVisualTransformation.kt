import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// Auto-format CPF
class CpfVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(11)
        var formatted = ""
        for (i in trimmed.indices) {
            formatted += trimmed[i]
            when (i) {
                2 -> formatted += "."  // Add dot after 3rd character
                5 -> formatted += "."  // Add dot after 6th character
                8 -> formatted += "-"  // Add - after 9th character
            }
        }

        val offsetMapping = object : OffsetMapping {
            // Maps original index (raw input) to transformed index (displayed text)
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 5 -> offset + 1
                    offset <= 8 -> offset + 2
                    else -> offset + 3
                }
            }

            // Maps transformed index (displayed text) to original index (raw input)
            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 6 -> offset - 1
                    offset <= 10 -> offset - 2
                    else -> offset - 3
                }
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}