import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CnpjVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(14)
        var formatted = ""
        for (i in trimmed.indices) {
            formatted += trimmed[i]
            when (i) {
                1 -> formatted += "."  // Add dot after 2nd character
                4 -> formatted += "."  // Add dot after 5th character
                7 -> formatted += "/"  // Add slash after 8th character
                11 -> formatted += "-" // Add dash after 12th character
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = when {
                offset <= 1 -> offset
                offset <= 4 -> offset + 1
                offset <= 7 -> offset + 2
                offset <= 11 -> offset + 3
                else -> offset + 4
            }

            override fun transformedToOriginal(offset: Int): Int = when {
                offset <= 2 -> offset
                offset <= 6 -> offset - 1
                offset <= 10 -> offset - 2
                offset <= 14 -> offset - 3
                else -> offset - 4
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}