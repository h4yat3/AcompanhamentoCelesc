import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// Auto-format dates with slashes
class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(8)
        val formatted = buildString {
            append(trimmed.take(2))
            if (trimmed.length > 2) append("/")
            append(trimmed.drop(2).take(2))
            if (trimmed.length > 4) append("/")
            append(trimmed.drop(4).take(4))
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 4 -> offset + 1
                    else -> offset + 2
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 5 -> offset - 1
                    else -> offset - 2
                }
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}