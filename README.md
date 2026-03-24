# 🏥 Saúde Transparente - App Mais Saúde

![Android](https://img.shields.io/badge/Android-API%2024%2B-green.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Gradle](https://img.shields.io/badge/Gradle-8.10.2-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

**Aplicativo Android para registro e acompanhamento de denúncias e solicitações relacionadas aos serviços de saúde pública.**

O **Saúde Transparente** é uma plataforma mobile que permite aos cidadãos registrar problemas e acompanhar o status de suas solicitações no sistema público de saúde, promovendo transparência e melhoria contínua dos serviços.

---

## 📱 Funcionalidades

### ✅ Implementadas

- **Novo Registro de Denúncias/Solicitações**
  - 9 categorias específicas com ícones customizados
  - Seleção de local (UBS, Hospital, Posto de Saúde)
  - Descrição detalhada com validação (mínimo 10 caracteres)
  - Anexo de foto via galeria
  - Data/hora automática

- **Acompanhamento de Registros**
  - Listagem de todos os registros salvos
  - Sistema de status visual com badges coloridos (Pendente, Em Análise, Resolvido)
  - Edição de registros existentes
  - Exclusão de registros
  - Mudança de status com um toque

- **Navegação Global**
  - Bottom Navigation Bar em todas as telas
  - 5 seções: Início, Novo, Histórico, Alertas, Perfil

- **Seção "Meus Dados"**
  - Card informativo na tela inicial
  - Foto de perfil
  - Nome e ID do usuário
  - Contador dinâmico de registros

- **Persistência de Dados**
  - Armazenamento local com SharedPreferences + Gson
  - Imagens salvas em armazenamento interno (compressão JPEG 80%)
  - Sincronização automática entre telas

### 🚧 Em Desenvolvimento

- Sistema de notificações push
- Tela de perfil completa
- Sistema de alertas
- Gráficos e estatísticas
- Campo de busca/filtro no histórico

---

## 🎨 Design & UI/UX

O aplicativo segue o design system **"Saúde Transparente"** com:

- **Cor Principal:** Teal `#00A38D`
- **Tipografia:** Roboto (Material Design)
- **Ícones:** Material Design + Customizados (9 categorias)
- **Status Colors:**
  - 🟠 Pendente: `#FF9800`
  - 🔵 Em Análise: `#2196F3`
  - 🟢 Resolvido: `#4CAF50`

### Categorias com Ícones Customizados

| Categoria | Ícone | Descrição |
|-----------|-------|-----------|
| 🏥 Infraestrutura | `ic_infraestrutura` | Problemas estruturais |
| 💊 Medicamentos | `ic_medicamentos` | Falta de medicamentos |
| 👨‍⚕️ Atendimento | `ic_atendimento` | Qualidade do atendimento |
| 📅 Agendamento | `ic_agendamento` | Dificuldades de agendamento |
| 💉 Vacinação | `ic_vacinacao` | Questões de vacinação |
| 🚑 Transporte | `ic_transporte` | Transporte sanitário |
| 🧹 Limpeza | `ic_limpeza` | Higiene e limpeza |
| 👥 Gestão de Fila | `ic_fila` | Filas e espera |
| ♿ Acessibilidade | `ic_acessibilidade` | Acessibilidade |

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java 17
- **SDK Android:** Min API 24 (Android 7.0), Target API 35 (Android 15)
- **Build System:** Gradle 8.10.2 + Kotlin DSL
- **UI Components:**
  - Material Components `1.9.0`
  - RecyclerView
  - CardView
  - BottomNavigationView
  - ConstraintLayout
- **Persistência:**
  - SharedPreferences
  - Gson `2.10.1` (serialização JSON)
- **Permissões:**
  - `READ_MEDIA_IMAGES` (Android 13+)
  - `READ_EXTERNAL_STORAGE` (Android < 13)

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

1. **Android Studio** Hedgehog ou superior
2. **JDK** 17+
3. **Android SDK** com API 35 instalada
4. **Emulador Android** ou dispositivo físico

### Passo a Passo

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/ClaudioMatheusDev/App_Saude_Transparente_CM.git
   cd App_Saude_Transparente_CM/MaisSaude
   ```

2. **Configure o Android SDK:**
   
   Crie o arquivo `local.properties` na raiz do projeto (se não existir):
   ```properties
   sdk.dir=C\:\\Users\\SeuUsuario\\AppData\\Local\\Android\\Sdk
   ```
   
   Ajuste o caminho de acordo com sua instalação do Android SDK.

3. **Abra o projeto no Android Studio:**
   - File → Open → Selecione a pasta `MaisSaude`
   - Aguarde o Gradle Sync completar

4. **Compile o projeto:**
   ```bash
   ./gradlew assembleDebug
   ```
   
   Ou no Android Studio: **Build → Make Project** (`Ctrl+F9`)

5. **Execute no emulador/dispositivo:**
   ```bash
   ./gradlew installDebug
   ```
   
   Ou no Android Studio: **Run → Run 'app'** (`Shift+F10`)

---

## 📁 Estrutura do Projeto

```
app/
├── src/main/
│   ├── java/com/example/appmaissaude/
│   │   ├── MainActivity.java              # Tela inicial
│   │   ├── NovoRegistroActivity.java      # Formulário de registro
│   │   ├── AcompanhamentoActivity.java    # Lista de registros
│   │   ├── Registro.java                  # Modelo de dados
│   │   ├── StatusRegistro.java            # Enum de status
│   │   ├── RegistroAdapter.java           # Adapter do RecyclerView
│   │   └── GerenciadorDados.java          # Persistência de dados
│   │
│   ├── res/
│   │   ├── drawable/                      # Ícones customizados
│   │   │   ├── ic_infraestrutura.xml
│   │   │   ├── ic_medicamentos.xml
│   │   │   ├── ic_atendimento.xml
│   │   │   ├── ic_agendamento.xml
│   │   │   ├── ic_vacinacao.xml
│   │   │   ├── ic_transporte.xml
│   │   │   ├── ic_limpeza.xml
│   │   │   ├── ic_fila.xml
│   │   │   ├── ic_acessibilidade.xml
│   │   │   ├── bg_icon_category.xml
│   │   │   ├── bg_status_*.xml
│   │   │   └── bg_button_*.xml
│   │   │
│   │   ├── layout/
│   │   │   ├── activity_main.xml
│   │   │   ├── activity_novo_registro.xml
│   │   │   ├── activity_acompanhamento.xml
│   │   │   └── item_registro.xml
│   │   │
│   │   ├── menu/
│   │   │   └── bottom_navigation_menu.xml
│   │   │
│   │   ├── color/
│   │   │   └── bottom_nav_color.xml
│   │   │
│   │   └── values/
│   │       ├── colors.xml
│   │       ├── strings.xml
│   │       └── themes.xml
│   │
│   └── AndroidManifest.xml
│
├── build.gradle.kts                       # Configuração Gradle do app
└── proguard-rules.pro                     # Regras de ofuscação
```

---

## 🎯 Melhorias Recentes

### v1.2.0 - UI/UX Alignment (Março 2026)

✅ **Ícones Customizados**
- 9 ícones vetoriais profissionais em Material Design
- Background circular com cor teal
- Tamanho otimizado (64dp)

✅ **Bottom Navigation**
- Navegação global em todas as 3 telas principais
- 5 seções com ícones e labels
- Cor dinâmica (selecionado/não selecionado)

✅ **Seção "Meus Dados"**
- Card informativo na home
- Foto de perfil circular
- Contador de registros em tempo real
- ID e nome do usuário

### v1.1.0 - Features Essenciais

✅ **Edição de Registros**
- Modo dual (criar/editar) na mesma Activity
- Parcelable para passar dados entre Activities
- Atualização via `GerenciadorDados.atualizarRegistro()`

✅ **Sistema de Status Visual**
- Enum `StatusRegistro` com 3 estados
- Badges coloridos no RecyclerView
- Mudança de status com toque no badge
- Progressão lógica: Pendente → Em Análise → Resolvido

### v1.0.0 - MVP

✅ Registro de denúncias com categorias
✅ Persistência com SharedPreferences
✅ Anexo de imagens da galeria
✅ Validação de campos
✅ RecyclerView otimizado

---

## 🐛 Problemas Conhecidos

- [ ] Android SDK deve ser configurado manualmente em `local.properties`
- [ ] Avisos de classpath no VS Code (não afetam compilação)
- [ ] Imagens grandes podem causar lentidão (considerar lazy loading)

---

## 🔜 Roadmap

### Próximas Features

- [ ] **Sistema de Notificações** (Firebase Cloud Messaging)
- [ ] **Tela de Perfil Completa** (editar nome, foto, configurações)
- [ ] **Sistema de Alertas** (denúncias urgentes destacadas)
- [ ] **Gráficos e Estatísticas** (MPAndroidChart)
- [ ] **Busca e Filtros** (por categoria, status, data)
- [ ] **Modo Escuro** (Dark Theme)
- [ ] **Compartilhamento de Registros** (compartilhar via WhatsApp, email)
- [ ] **Integração com APIs Governamentais** (autenticação gov.br)
- [ ] **Múltiplas Fotos** (galeria com 3+ imagens por registro)
- [ ] **Mapa Interativo** (Google Maps para localização precisa)

---

## 📊 Conformidade UI/UX

**Status Atual:** ~**80-85%** em conformidade com o template "Saúde Transparente"

✅ Implementado:
- Cores e branding oficial
- Bottom Navigation
- Ícones customizados
- Seção "Meus Dados"
- Sistema de status visual

⏳ Pendente:
- Sistema de alertas
- Indicador de progresso visual
- Mapa interativo
- Múltiplas fotos/vídeos
- Sistema de feedback com estrelas

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Faça um Fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

### Convenções de Código

- Código em **Java** seguindo padrões do Android
- Layouts XML com indentação de 4 espaços
- Nomes de variáveis em **camelCase**
- Nomes de classes em **PascalCase**
- Comentários em português para clareza
- Commits descritivos e atômicos

---

## 📄 Licença

Este projeto está sob a licença **MIT**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👨‍💻 Autor

**Claudio Matheus**
- GitHub: [@ClaudioMatheusDev](https://github.com/ClaudioMatheusDev)
- Repositório: [App_Saude_Transparente_CM](https://github.com/ClaudioMatheusDev/App_Saude_Transparente_CM)

---

## 📞 Suporte

Encontrou um bug ou tem uma sugestão? 

- Abra uma [Issue](https://github.com/ClaudioMatheusDev/App_Saude_Transparente_CM/issues)
- Entre em contato via GitHub

---

## 🙏 Agradecimentos

- Comunidade Android pelo suporte e documentação
- Material Design pela inspiração visual
- Usuários beta pelos feedbacks valiosos

---

<p align="center">
  Feito com ❤️ para melhorar a saúde pública através da tecnologia
</p>

<p align="center">
  <sub>v1.2.0 - Março 2026</sub>
</p>
