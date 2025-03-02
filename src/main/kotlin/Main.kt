import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import java.io.File
import kotlinx.serialization.json.Json
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.encodeToString
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.delay


fun main() = application {
    val windowState = remember { WindowState(size = DpSize(900.dp, 900.dp)) }
    val initialClients = remember { loadClientData() }
    val clientList = remember { mutableStateOf(initialClients) }
    var notificationMessage by remember { mutableStateOf<String?>(null) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Cadastro",
        state = windowState,
    ) {
        var cliente by remember { mutableStateOf(Cliente()) }
        var searchQuery by remember { mutableStateOf("") }

        LaunchedEffect(notificationMessage) {
            notificationMessage?.let {
                delay(3000)
                notificationMessage = null
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                formSection(
                    modifier = Modifier.weight(0.7f),
                    state = cliente,
                    onUpdate = { updatedCliente ->
                        cliente = updatedCliente
                        searchQuery = updatedCliente.nome
                    },
                    onSave = { savedCliente ->
                        clientList.value = loadClientData()
                    },
                    showNotification = { message ->
                        notificationMessage = message
                    }
                )
                clientListSection(
                    modifier = Modifier.weight(0.3f),
                    searchQuery = searchQuery,
                    clientList = clientList.value,
                    onItemClick = { selectedClient ->
                        cliente = selectedClient
                        searchQuery = selectedClient.nome
                    }
                )
            }
            notificationMessage?.let { message ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .background(Color(0xFF4CAF50).copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = message,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun formSection(
    state: Cliente,
    onUpdate: (Cliente) -> Unit,
    onSave: (Cliente) -> Unit,
    showNotification: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSaveEnabled = state.idCliente.isNotBlank() && state.nome.isNotBlank()
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .padding(12.dp)
    ) {
//----------------------------------------------------------------------------------------------------------------
//Seção cliente
        item(span = { GridItemSpan(2) }) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row {
                            Text(
                                text = "Dados do Cliente",
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                            Spacer(modifier.padding(10.dp))
                            IconButton(onClick = {
                                onUpdate(Cliente())
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Resetar Campos"
                                )
                            }
                        }
//  ID Cliente
                        validatedTextField(
                            label = "ID Cliente",
                            value = state.idCliente,
                            onValueChange = { newText ->
                                onUpdate(state.copy(idCliente = newText.filter { it.isDigit() }.take(6)))
                            },
                            isRequired = true,
                            validationError = state.errors["idCliente"]
                        )
//  Nome
                        validatedTextField(
                            label = "Nome Completo",
                            value = state.nome,
                            onValueChange = { onUpdate(state.copy(nome = it)) },
                            isRequired = true,
                            validationError = state.errors["nome"]
                        )
//  Second Row
//  Data de Nascimento
                        dateTextField(
                            label = "Data de Nascimento",
                            value = state.dataNascimento,
                            onValueChange = { newDate ->
                                onUpdate(state.copy(dataNascimento = newDate)) },
                            validationError = state.errors["dataNascimento"]
                        )
//  CPF/CNPJ
                        validatedTextField(
                            label = "CPF/CNPJ",
                            value = state.cpfCnpj,
                            onValueChange = { newText ->
                                onUpdate(state.copy(cpfCnpj = newText.filter { it.isDigit() }.take(14))) },
                            validationError = state.errors[""],
                            visualTransformation = when (state.cpfCnpj.length) {
                                in 0..11 -> CpfVisualTransformation()
                                in 12..14 -> CnpjVisualTransformation()
                                else -> VisualTransformation.None
                            }
                        )
//  Third Row
//  EMAIL
                        validatedTextField(
                            label = "E-mail",
                            value = state.eMail,
                            onValueChange = { onUpdate(state.copy(eMail = it)) },
                            validationError = state.errors[""],
                        )
                    }
                }
//-----------------------------------------------------------------------------------------------------------------
//  UNIDADE CONSUMIDORA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Unidades Consumidoras",
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            maxItemsInEachRow = 5
                        ) {
                            state.unidadeConsumidora.forEachIndexed { index, uc ->
                                Card(
                                    modifier = Modifier
                                        .width(200.dp)
                                        .height(130.dp)
                                        .padding(1.dp),
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
                                    .height(130.dp)
                                    .padding(1.dp)
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
            }
        }
//-------------------------------------------------------------------------------------------------------//
// Homologação Celesc
        item(span = { GridItemSpan(2) }) {
            Column {
                Text(
                    text = "Homologação Micro-Geração Celesc",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
// DATA DE REQUISIÇÃO DO PROTOCOLO
                dateTextField(
                    label = "Data de Solicitação do Protocolo",
                    value = state.dataRequisicaoProtocolo,
                    onValueChange = { newDate ->
                        onUpdate(state.copy(dataRequisicaoProtocolo = newDate)) },
                    validationError = state.errors[""],
                )
//  PROTOCOLO
                validatedTextField(
                    label = "Protocolo",
                    value = state.protocolo,
                    onValueChange = { newText ->
                        onUpdate(state.copy(protocolo = newText.filter { it.isDigit() }))
                    },
                    validationError = state.errors["protocolo"]
                )
// DATA DE ENTRADA DO PROJETO NA CELESC
                dateTextField(
                    label = "Data de entrada do projeto na Celesc",
                    value = state.dataEntradaProjeto,
                    onValueChange = { newDate ->
                        onUpdate(state.copy(dataEntradaProjeto = newDate)) },
                    validationError = state.errors[""],
                )
// DATA DE APROVAÇÃO DO PROJETO NA CELESC
                dateTextField(
                    label = "Data de aprovação do projeto",
                    value = state.dataAprovacaoProjeto,
                    onValueChange = { newDate ->
                        onUpdate(state.copy(dataAprovacaoProjeto = newDate)) },
                    validationError = state.errors[""],
                )
// DATA DE SOLICITAÇÃO DE VISTORIA
                dateTextField(
                    label = "Data de Solicitação de Vistoria",
                    value = state.dataRequisicaoVistoria,
                    onValueChange = { newDate ->
                        onUpdate(state.copy(dataRequisicaoVistoria = newDate)) },
                    validationError = state.errors[""],
                )
// DATA DE APROVAÇÃO DE VISTORIA
                dateTextField(
                    label = "Data de Aprovação da Vistoria",
                    value = state.dataAprovacaoVistoria,
                    onValueChange = { newDate ->
                        onUpdate(state.copy(dataAprovacaoVistoria = newDate)) },
                    validationError = state.errors[""],
                )
//  TRT / CFT
                validatedTextField(
                    label = "TRT/CFT",
                    value = state.trtCft,
                    onValueChange = {
                        onUpdate(state.copy(trtCft = it))
                    },
                    validationError = state.errors[""]
                )
            }
        }
//----------------------------------------------------------------------------------------------------------------
        item(span = { GridItemSpan(2) }) {
            Column {
//  CREDENCIAIS CELESC
                Text(
                    text = "Credenciais Celesc",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
//  USUARIO CELESC
                validatedTextField(
                    label = "Usuário Celesc",
                    value = state.loginCelesc,
                    onValueChange = { onUpdate(state.copy(loginCelesc = it)) },
                    validationError = state.errors["Usuario Celesc"]
                )
//  SENHA CELESC
                validatedTextField(
                    label = "Senha Celesc",
                    value = state.senhaCelesc,
                    onValueChange = { onUpdate(state.copy(senhaCelesc = it)) },
                    validationError = state.errors["Senha Celesc"]
                )
            }
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
                                .padding(1.dp),
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
    // potencia do inversor
                                )
                                validatedTextField(
                                    label = "Potência do Inversor",
                                    value = inversor.potencia,
                                    onValueChange = { newPotencia ->
                                        val updatedList = state.inversores.toMutableList().apply {
                                            this[index] = this[index].copy(potencia = newPotencia)
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
                            .padding(1.dp)
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
                        try {
                            saveClientData(validated)
                            showNotification("Cliente salvo com sucesso!") // Success
                            onSave(validated)
                        } catch (e: Exception) {
                            showNotification("Erro ao salvar: ${e.message}") // Error
                        }
                    } else {
                        showNotification("Corrija os campos obrigatórios") // Validation error
                        onUpdate(validated)
                    }
                },
                enabled = isSaveEnabled,
            ) {
                Text("Salvar")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun clientListSection(
    modifier: Modifier = Modifier,
    searchQuery: String,
    clientList: List<Cliente>,
    onItemClick: (Cliente) -> Unit = {}
) {
    val filteredClients by remember(searchQuery, clientList) {
        derivedStateOf {
            if (searchQuery.isEmpty()) {
                clientList
            } else {
                val matchedClients = clientList.filter { client ->
                    client.nome.contains(searchQuery, ignoreCase = true) ||
                            client.idCliente.contains(searchQuery, ignoreCase = true)
                }
                if (matchedClients.isNotEmpty()) matchedClients else clientList
            }
        }
    }
    Column(modifier = modifier.fillMaxSize()) {
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Gray
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            items(
                items = filteredClients,
                key = { client -> client.idCliente } // Required for animation
            ) { client ->
                clientListItem(
                    client = client,
                    onItemClick = onItemClick,
                    modifier = Modifier.animateItemPlacement() // Animation here
                )
            }
        }
    }
}

@Composable
fun clientListItem(
    client: Cliente,
    onItemClick: (Cliente) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(client) }
            .padding(8.dp) // Add some padding for spacing
    ) {
        Text(
            text = "${client.idCliente} - ${client.nome}",
            style = MaterialTheme.typography.subtitle2
        )
        Text(
            text = "CPF/CNPJ: ${client.cpfCnpj}",
            style = MaterialTheme.typography.caption
        )
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
    Column(modifier = modifier.padding(vertical = 2.dp)) {
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
                modifier = Modifier.padding(start = 5.dp)
            )
        }
    }
}

private fun validateCliente(cliente: Cliente): Cliente {
    val errors = mutableMapOf<String, String>()
    with(cliente) {
        if (idCliente.isBlank()) errors["idCliente"] = "ID obrigatório"

        if (nome.isBlank()) errors["nome"] = "Nome do Cliente é obrigatório"
    }
    return cliente.copy(errors = errors)
}

fun saveClientData(cliente: Cliente) {
    try {
        val desktopPath = System.getProperty("user.home") + "/Desktop"
        val file = File(desktopPath, "cliente_data.txt").apply {
            parentFile?.mkdirs()
        }
        val existing = if (file.exists()) {
            try {
                Json.decodeFromString<MutableList<Cliente>>(file.readText())
            } catch (e: Exception) {
                println("Error reading existing data: ${e.message}")
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
        val index = existing.indexOfFirst { it.idCliente == cliente.idCliente }
        if (index != -1) {
            existing[index] = cliente
            println("Updated existing client with ID: ${cliente.idCliente}")
        } else {
            existing.add(cliente)
            println("Added new client with ID: ${cliente.idCliente}")
        }

        val jsonString = Json.encodeToString(existing)
        file.writeText(jsonString)
    } catch (e: Exception) {
        throw Exception("Falha ao salvar: ${e.message}") // Rethrow with custom message
    }
}

fun loadClientData(): List<Cliente> {
    return try {
        val desktopPath = System.getProperty("user.home") + "/Desktop"
        val file = File(desktopPath, "cliente_data.txt")
        if (file.exists()) {
            val jsonString = file.readText()
            Json.decodeFromString<List<Cliente>>(jsonString)
        } else {
            println("File does not exist, returning empty list.")
            emptyList()
        }
    } catch (e: Exception) {
        println("Error loading client data: ${e.message}")
        emptyList()
    }
}

@Composable
fun dateTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    validationError: String?,
    modifier: Modifier = Modifier
) {
    validatedTextField(
        label = label,
        value = value,
        onValueChange = { newText ->
            onValueChange(newText.filter { it.isDigit() }.take(8))
        },
        validationError = validationError,
        visualTransformation = DateVisualTransformation(),
        placeholder = "DD/MM/AAAA",
        modifier = modifier
    )
}