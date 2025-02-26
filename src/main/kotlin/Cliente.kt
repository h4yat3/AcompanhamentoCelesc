import kotlinx.serialization.Serializable

@Serializable
data class Cliente(
    val idCliente: String = "",
    val nome: String = "",
    val cpfCnpj: String = "",
    val eMail: String = "",
    val dataNascimento: String = "",
    val dataRequisicaoProtocolo: String = "",
    val dataEntradaProjeto: String = "",
    val dataAprovacaoProjeto: String = "",
    val dataRequisicaoVistoria: String = "",
    val dataAprovacaoVistoria: String = "",
    val protocolo: String = "",
    val trtCft: String = "",
    val unidadeConsumidora: List<UnidadeConsumidora> = listOf(UnidadeConsumidora()),
    val loginCelesc: String = "",
    val senhaCelesc: String = "",
    val inversores: List<Inversor> = listOf(Inversor()),
    val errors: Map<String, String> = emptyMap()
)

@Serializable
data class Inversor(
    val marca: String = "",
    val sn: String = "",
    val login: String = "",
    val senha: String = ""
)

@Serializable
data class UnidadeConsumidora(
    val numero: String = ""
)
