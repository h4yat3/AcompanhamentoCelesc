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
import androidx.compose.material.icons.filled.Add

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
    onUpdate: (Cliente) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(16.dp)
    ) {
        item(span = { GridItemSpan(4) }) {
            Text(
                text = "Dados do Cliente",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
//  ID Cliente
        item(span = { GridItemSpan(2) }) {
            validatedTextField(
                label = "ID Cliente",
                value = state.idCliente,
                onValueChange = { onUpdate(state.copy(idCliente = it)) },
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
                validationError = state.errors["cpfCnpj"],
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
                validationError = state.errors["eMail"]
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
//  DADOS CELESC
        item(span = { GridItemSpan(4) }) {
            Text(
                text = "Credenciais Celesc",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 8.dp)
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
                                .width(210.dp)
                                .height(130.dp)
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
                            .height(150.dp)
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
                    // ✅ Loop through existing inverters and display fields
                    state.inversores.forEachIndexed { index, inversor ->
                        Card(
                            modifier = Modifier
                                .width(350.dp)
                                .height(450.dp) // Adjusted height to fit fields
                                .padding(8.dp),
                            elevation = 4.dp,
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Inversor ${index + 1}",
                                    style = MaterialTheme.typography.h6,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                // ✅ Marca do Inversor
                                validatedTextField(
                                    label = "Marca do Inversor",
                                    value = inversor.marca,
                                    onValueChange = { newMarca ->
                                        val updatedList = state.inversores.toMutableList().apply {
                                            this[index] = this[index].copy(marca = newMarca)
                                        }
                                        onUpdate(state.copy(inversores = updatedList))
                                    },
                                    validationError = ""
                                )

                                // ✅ Número de Série
                                validatedTextField(
                                    label = "Número de Série",
                                    value = inversor.sn,
                                    onValueChange = { newSn ->
                                        val updatedList = state.inversores.toMutableList().apply {
                                            this[index] = this[index].copy(sn = newSn)
                                        }
                                        onUpdate(state.copy(inversores = updatedList))
                                    },
                                    validationError = ""
                                )

                                // ✅ Login do Inversor
                                validatedTextField(
                                    label = "Login do Inversor",
                                    value = inversor.login,
                                    onValueChange = { newLogin ->
                                        val updatedList = state.inversores.toMutableList().apply {
                                            this[index] = this[index].copy(login = newLogin)
                                        }
                                        onUpdate(state.copy(inversores = updatedList))
                                    },
                                    validationError = ""
                                )

                                // ✅ Senha do Inversor
                                validatedTextField(
                                    label = "Senha do Inversor",
                                    value = inversor.senha,
                                    onValueChange = { newSenha ->
                                        val updatedList = state.inversores.toMutableList().apply {
                                            this[index] = this[index].copy(senha = newSenha)
                                        }
                                        onUpdate(state.copy(inversores = updatedList))
                                    },
                                    validationError = ""
                                )
                            }
                        }
                    }

                    // ✅ Card Button to Add New Inversor
                    Card(
                        modifier = Modifier
                            .width(350.dp)
                            .height(450.dp)
                            .padding(8.dp)
                            .clickable {
                                // Add a new inversor when clicked
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
        // Submit Button
        item(span = { GridItemSpan(4) }) {
            Button(
                onClick = {
                    val validated = validateCliente(state)
                    if (validated.errors.isEmpty()) {
                        printClienteData(validated)
                    } else {
                        onUpdate(validated)
                    }
                },
                modifier = Modifier.fillMaxWidth(0.5f).height(70.dp).padding(8.dp)
            ) {
                Text("Enviar")
            }
        }
    }

}





// Updated ValidatedTextField with better grid support
@Composable
fun validatedTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isRequired: Boolean = false,
    validationError: String?,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = if (isRequired) "$label *" else label) },
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
                modifier = Modifier.padding(start = 8.dp)
            )
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
    visualTransformation: VisualTransformation = VisualTransformation.None,
    placeholder: String = "" // Add placeholder parameter
) {
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = if (isRequired) "$label *" else label) },
            placeholder = { Text(placeholder) }, // Set placeholder text
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
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

private fun validateCliente(cliente: Cliente): Cliente {
    val errors = mutableMapOf<String, String>()

    if (cliente.idCliente.isBlank()) {
        errors["idCliente"] = "ID Cliente é obrigatório"
    }
    if (cliente.nome.isBlank()) {
        errors["nome"] = "Nome é obrigatório"
    }

    return cliente.copy(errors = errors)
}

// Modified print function:
private fun printClienteData(cliente: Cliente) {
    with(cliente) {
        println("=== Dados do Cliente ===")
        println("ID Cliente: $idCliente")
        println("Nome: $nome")
        println("CPF/CNPJ: $cpfCnpj")
        println("E-mail: $eMail")
        println("Data Nascimento: $dataNascimento")
        println("Protocolo: $protocolo")
        println("Login Celesc: $loginCelesc")
        println("Senha Celesc: $senhaCelesc")

        val nonEmptyInversores = inversores.filter { inversor ->
            inversor.marca.isNotBlank() ||
                    inversor.sn.isNotBlank() ||
                    inversor.login.isNotBlank() ||
                    inversor.senha.isNotBlank()
        }

        println("\nInversores Registrados:")
        nonEmptyInversores.forEachIndexed { index, inversor ->
            println("Inversor ${index + 1}:")
            println("  Marca: ${inversor.marca}")
            println("  S/N: ${inversor.sn}")
            println("  Login: ${inversor.login}")
            println("  Senha: ${inversor.senha}")
        }
    }
}