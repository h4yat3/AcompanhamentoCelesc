import kotlinx.serialization.Serializable

@Serializable
data class Cliente(
    val idCliente: String = "",
    val nome: String = "",
    val dataNascimento: String = "",
    val cpfCnpj: String = "",
    val eMail: String = "",
    val dataRequisicaoProtocolo: String = "",
    val protocolo: String = "",
    val dataEntradaProjeto: String = "",
    val dataAprovacaoProjeto: String = "",
    val dataRequisicaoVistoria: String = "",
    val dataAprovacaoVistoria: String = "",
    val trtCft: String = "",
    val loginCelesc: String = "",
    val senhaCelesc: String = "",
    val unidadeConsumidora: List<UnidadeConsumidora> = emptyList(),
    val inversores: List<Inversor> = emptyList(),
    val errors: Map<String, String> = emptyMap()
)

@Serializable
data class UnidadeConsumidora(
    val numero: String = ""
)

@Serializable
data class Inversor(
    val marca: String = "",
    val potencia: String ="",
    val sn: String = "",
    val login: String = "",
    val senha: String = ""
)