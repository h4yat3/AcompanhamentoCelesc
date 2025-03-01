import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import java.io.File
import kotlinx.serialization.json.Json
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check



fun main() = application {
    val windowState = remember { WindowState(size = DpSize(900.dp, 600.dp)) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Cadastro",
        state = windowState,
    ) {
        var cliente by remember { mutableStateOf(Cliente()) }
        formSection(
            state = cliente,
            onUpdate = { updatedCliente -> cliente = updatedCliente }
        )
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun formSection(
    state: Cliente,
    onUpdate: (Cliente) -> Unit
) {
    var isEditMode by remember { mutableStateOf(true) }
    var originalCliente by remember { mutableStateOf<Cliente?>(null) }

    LaunchedEffect(state.idCliente) {
        if (state.idCliente.isNotBlank() && originalCliente == null) {
            isEditMode = false
            originalCliente = state
        }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(12.dp)
    ) {
        item(span = { GridItemSpan(4) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Dados do Cliente",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = {
                        isEditMode = !isEditMode
                    }) {
                        Icon(
                            imageVector = if (isEditMode) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = if (isEditMode) "Desbloquear Edição" else "Bloquear Edição",
                            tint = if (isEditMode) MaterialTheme.colors.primary else MaterialTheme.colors.error
                        )
                    }

                    IconButton(onClick = {
                        val foundCliente = searchClientData(state.idCliente, state.nome)
                        if (foundCliente != null) {
                            onUpdate(foundCliente)
                        } else {
                            println("No matching record found.")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar Cliente"
                        )
                    }
                    IconButton(onClick = {
                        onUpdate(Cliente())
                        isEditMode = true // Reset to edit mode when clearing form
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Resetar Campos"
                        )
                    }
                }
            }
        }
//  ID Cliente
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "ID Cliente",
                value = state.idCliente,
                onValueChange = { newText ->
                    onUpdate(state.copy(idCliente = newText.filter { it.isDigit() }.take(6)))
                },
                isRequired = true,
                validationError = state.errors["idCliente"]
            )
        }
//  Nome
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "Nome Completo",
                value = state.nome,
                onValueChange = { onUpdate(state.copy(nome = it)) },
                isRequired = true,
                validationError = state.errors["nome"]
            )
        }
//  Second Row
//  Data de Nascimento
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "Data de Nascimento",
                value = state.dataNascimento,
                onValueChange = { newText ->
                    onUpdate(state.copy(dataNascimento = newText.filter { it.isDigit() }.take(8)))
                },
                validationError = state.errors["dataNascimento"],
                visualTransformation = DateVisualTransformation(),
                placeholder = "DD/MM/AAAA"
            )
        }
//  CPF/CNPJ
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "CPF/CNPJ",
                value = state.cpfCnpj,
                onValueChange = { newText ->
                    onUpdate(state.copy(cpfCnpj = newText.filter { it.isDigit() }.take(14)))
                },
                validationError = state.errors[""],
                visualTransformation = when (state.cpfCnpj.length) {
                    in 0..11 -> CpfVisualTransformation()
                    in 12..14 -> CnpjVisualTransformation()
                    else -> VisualTransformation.None
                }
            )
        }
//  Third Row
//  EMAIL
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "E-mail",
                value = state.eMail,
                onValueChange = { onUpdate(state.copy(eMail = it)) },
                validationError = state.errors[""],
            )
        }
//-------------------------------------------------------------------------------------------------------//
        item(span = { GridItemSpan(4) }) {
            Text(
                text = "Homologação Micro-Geração Celesc",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
// DATA DE REQUISIÇÃO DO PROTOCOLO
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "Data de Solicitação do Protocolo",
                value = state.dataRequisicaoProtocolo,
                onValueChange = { newText ->
                    onUpdate(state.copy(dataRequisicaoProtocolo = newText.filter { it.isDigit() }.take(8)))
                },
                validationError = state.errors[""],
                visualTransformation = DateVisualTransformation(),
                placeholder = "DD/MM/AAAA"
            )
        }
//  PROTOCOLO
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "Protocolo",
                value = state.protocolo,
                onValueChange = { newText ->
                    onUpdate(state.copy(protocolo = newText.filter { it.isDigit() }))
                },
                validationError = state.errors["protocolo"]
            )
        }
// DATA DE ENTRADA DO PROJETO NA CELESC
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "Data de entrada do projeto na Celesc",
                value = state.dataEntradaProjeto,
                onValueChange = { newText ->
                    onUpdate(state.copy(dataEntradaProjeto = newText.filter { it.isDigit() }.take(8)))
                },
                validationError = state.errors[""],
                visualTransformation = DateVisualTransformation(),
                placeholder = "DD/MM/AAAA"
            )
        }
// DATA DE APROVAÇÃO DO PROJETO NA CELESC
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "Data de aprovação do projeto",
                value = state.dataAprovacaoProjeto,
                onValueChange = { newText ->
                    onUpdate(state.copy(dataAprovacaoProjeto = newText.filter { it.isDigit() }.take(8)))
                },
                validationError = state.errors[""],
                visualTransformation = DateVisualTransformation(),
                placeholder = "DD/MM/AAAA"
            )
        }
// DATA DE SOLICITAÇÃO DE VISTORIA
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "Data de Solicitação de Vistoria",
                value = state.dataRequisicaoVistoria,
                onValueChange = { newText ->
                    onUpdate(state.copy(dataRequisicaoVistoria = newText.filter { it.isDigit() }.take(8)))
                },
                validationError = state.errors[""],
                visualTransformation = DateVisualTransformation(),
                placeholder = "DD/MM/AAAA"
            )
        }
// DATA DE APROVAÇÃO DE VISTORIA
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "Data de Aprovação da Vistoria",
                value = state.dataAprovacaoVistoria,
                onValueChange = { newText ->
                    onUpdate(state.copy(dataAprovacaoVistoria = newText.filter { it.isDigit() }.take(8)))
                },
                validationError = state.errors[""],
                visualTransformation = DateVisualTransformation(),
                placeholder = "DD/MM/AAAA"
            )
        }
//  TRT / CFT
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "TRT/CFT",
                value = state.trtCft,
                onValueChange = { onUpdate(state.copy(trtCft = it))
                },
                validationError = state.errors[""]
            )
        }
    //  UNIDADE CONSUMIDORA
        item(span = { GridItemSpan(4) }) {
            Column {
                Text(
                    text = "Unidades Consumidoras",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    maxItemsInEachRow = 5
                ) {
                    state.unidadeConsumidora.forEachIndexed { index, uc ->
                        Card(
                            modifier = Modifier
                                .width(200.dp)
                                .height(180.dp)
                                .padding(8.dp),
                            elevation = 4.dp,
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Unidade ${index + 1}",
                                    style = MaterialTheme.typography.subtitle1,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                validatedTextField(
                                    label = "Número UC",
                                    value = uc.numero,
                                    onValueChange = { newNumero ->
                                        val updatedList = state.unidadeConsumidora.toMutableList().apply {
                                            this[index] = this[index].copy(numero = newNumero)
                                        }
                                        onUpdate(state.copy(unidadeConsumidora = updatedList))
                                    },
                                    validationError = state.errors["uc$index"]
                                )
                            }
                        }
                    }

    // Add new UC button
                    Card(
                        modifier = Modifier
                            .width(200.dp)
                            .height(180.dp)
                            .padding(8.dp)
                            .clickable {
                                onUpdate(state.copy(unidadeConsumidora = state.unidadeConsumidora + UnidadeConsumidora()))
                            },
                        elevation = 4.dp,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add UC"
                            )
                        }
                    }
                }
            }
        }
//----------------------------------------------------------------------------------------------------------------
//  CREDENCIAIS CELESC
        item(span = { GridItemSpan(4) }) {
            Text(
                text = "Credenciais Celesc",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
//  USUARIO CELESC
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "Usuário Celesc",
                value = state.loginCelesc,
                onValueChange = { onUpdate(state.copy(loginCelesc = it)) },
                validationError = state.errors["Usuario Celesc"]
            )
        }
//  SENHA CELESC
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "Senha Celesc",
                value = state.senhaCelesc,
                onValueChange = { onUpdate(state.copy(senhaCelesc = it)) },
                validationError = state.errors["Senha Celesc"]
            )
        }
//----------------------------------------------------------------------------------------------------//
        //--------------------------- SEÇÃO INVERSORES -----------------------------------------//
        item(span = { GridItemSpan(4) }) {
            Column {
                Text(
                    text = "Inversor",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    maxItemsInEachRow = 5
                ) {
    //  Loop through existing inverters and display fields
                    state.inversores.forEachIndexed { index, inversor ->
                        Card(
                            modifier = Modifier
                                .width(350.dp)
                                .height(400.dp)
                                .padding(8.dp),
                            elevation = 4.dp,
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Inversor ${index + 1}",
                                    style = MaterialTheme.typography.h6,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

    //  Marca do Inversor
                                validatedTextField(
                                    label = "Marca do Inversor",
                                    value = inversor.marca,
                                    onValueChange = { newMarca ->
                                        val updatedList = state.inversores.toMutableList().apply {
                                            this[index] = this[index].copy(marca = newMarca)
                                        }
                                        onUpdate(state.copy(inversores = updatedList))
                                    },
                                    validationError = state.errors[""]
                                )

    //  Número de Série
                                validatedTextField(
                                    label = "Número de Série",
                                    value = inversor.sn,
                                    onValueChange = { newSn ->
                                        val updatedList = state.inversores.toMutableList().apply {
                                            this[index] = this[index].copy(sn = newSn)
                                        }
                                        onUpdate(state.copy(inversores = updatedList))
                                    },
                                    validationError = state.errors[""]
                                )

    //  Login do Inversor
                                validatedTextField(
                                    label = "Login do Inversor",
                                    value = inversor.login,
                                    onValueChange = { newLogin ->
                                        val updatedList = state.inversores.toMutableList().apply {
                                            this[index] = this[index].copy(login = newLogin)
                                        }
                                        onUpdate(state.copy(inversores = updatedList))
                                    },
                                    validationError = state.errors[""]
                                )

    //  Senha do Inversor
                                validatedTextField(
                                    label = "Senha do Inversor",
                                    value = inversor.senha,
                                    enabled = isEditMode,
                                    onValueChange = { newSenha ->
                                        val updatedList = state.inversores.toMutableList().apply {
                                            this[index] = this[index].copy(senha = newSenha)
                                        }
                                        onUpdate(state.copy(inversores = updatedList))
                                    },
                                    validationError = state.errors[""]
                                )
                            }
                        }
                    }

    //  BOTÃO PARA ADICIONAR NOVO INVERSOR
                    Card(
                        modifier = Modifier
                            .width(350.dp)
                            .height(400.dp)
                            .padding(8.dp)
                            .clickable {
                                onUpdate(state.copy(inversores = state.inversores + Inversor()))
                            },
                        elevation = 4.dp,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Inversor"
                            )
                        }
                    }
                }
            }
        }
//----------------------------------------------------------------------------------------------------//
//   BOTÃO DE ENVIAR CADASTRO
        item(span = { GridItemSpan(4) }) {
            Button(
                onClick = {
                    val validated = validateCliente(state)
                    if (validated.errors.isEmpty()) {
                        saveClientData(validated)
                        isEditMode = false
                        originalCliente = validated
                    } else {
                        onUpdate(validated)
                    }
                },
                enabled = isEditMode,
            ) {
                Text("Salvar")
            }
        }
    }
}



@Composable
fun validatedTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isRequired: Boolean = false,
    validationError: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    placeholder: String = ""
) {
    Column(modifier = modifier.padding(vertical = 1.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            label = { Text(text = if (isRequired) "$label *" else label) },
            placeholder = { Text(placeholder) },
            isError = validationError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = visualTransformation
        )

        validationError?.let {
            Text(
                text = it,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}



private fun validateCliente(cliente: Cliente): Cliente {
    val errors = mutableMapOf<String, String>()

    with(cliente) {
        if (idCliente.isBlank()) errors["idCliente"] = "ID obrigatório"
        if (nome.isBlank()) errors["nome"] = "Nome obrigatório"
        if (cpfCnpj.length !in 11..14) errors["cpfCnpj"] = "CPF/CNPJ inválido"
        if (!eMail.contains("@")) errors["eMail"] = "E-mail inválido"
    }
    return cliente.copy(errors = errors)
}



fun saveClientData(cliente: Cliente) {
    try {
        val file = File(System.getProperty("user.home") + "/Desktop/cliente_data.txt")
        val existing = if (file.exists()) Json.decodeFromString<MutableList<Cliente>>(file.readText())
        else mutableListOf()
        val index = existing.indexOfFirst { it.idCliente == cliente.idCliente }
        if (index != -1) {
            existing[index] = cliente
        } else {
            existing.add(cliente)
        }
    } catch (e: Exception) {
        println("Error saving data: ${e.message}")
    }
}



fun searchClientData(id: String, nome: String): Cliente? {
    val file = File("${System.getProperty("user.home")}/Desktop/cliente_data.txt")

    if (!file.exists()) {
        println("Error: File not found at ${file.absolutePath}")
        return null
    }
    return try {
        val clientes: List<Cliente> = Json.decodeFromString(file.readText())
        clientes.firstNotNullOfOrNull { cliente ->
            when {
                id.isNotBlank() && cliente.idCliente == id.padStart(4, '0') -> cliente
                nome.isNotBlank() && cliente.nome.equals(nome, ignoreCase = true) -> cliente
                else -> null
            }
        }
    } catch (e: Exception) {
        println("Error parsing JSON: ${e.message}")
        null
    }
}
