# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/).

---

## [1.2.0] - 2026-03-23

### 🎨 Added - UI/UX Alignment com Template "Saúde Transparente"

#### Ícones Customizados de Categorias
- Criados 9 ícones vetoriais customizados em Material Design
  - `ic_infraestrutura.xml` - Ícone de prédio/estrutura
  - `ic_medicamentos.xml` - Ícone de pílula/cápsula
  - `ic_atendimento.xml` - Ícone de pessoa com estetoscópio
  - `ic_agendamento.xml` - Ícone de calendário
  - `ic_vacinacao.xml` - Ícone de seringa
  - `ic_transporte.xml` - Ícone de ambulância
  - `ic_limpeza.xml` - Ícone de produtos de limpeza
  - `ic_fila.xml` - Ícone de fila de pessoas
  - `ic_acessibilidade.xml` - Ícone de cadeira de rodas
- Adicionado background circular para ícones (`bg_icon_category.xml`)
- Todos os ícones em cor teal (#00A38D) com tamanho 64dp

#### Bottom Navigation Bar
- Implementada navegação inferior global em todas as telas principais
- Menu com 5 seções:
  - 🏠 Início (MainActivity)
  - ➕ Novo (NovoRegistroActivity)
  - 📋 Histórico (AcompanhamentoActivity)
  - 🔔 Alertas (placeholder - em desenvolvimento)
  - 👤 Perfil (placeholder - em desenvolvimento)
- Criado arquivo `bottom_navigation_menu.xml`
- Criado seletor de cores `bottom_nav_color.xml` (teal quando selecionado, cinza quando não)
- Integrado em:
  - `activity_main.xml`
  - `activity_novo_registro.xml`
  - `activity_acompanhamento.xml`

#### Seção "Meus Dados" na Home
- Adicionado CardView informativo na tela inicial (`activity_main.xml`)
- Componentes incluídos:
  - Foto de perfil circular com ícone
  - Label "MEUS DADOS"
  - Nome do usuário (João Silva)
  - ID do usuário (12345678)
  - Contador dinâmico de registros
- Contador atualizado automaticamente via `GerenciadorDados.carregarRegistros()`
- Método `carregarNumeroRegistros()` em `MainActivity.java`
- Atualização em `onResume()` para manter dados sincronizados

### 🔧 Changed
- `activity_novo_registro.xml` atualizado para usar ícones customizados
  - Substituídos todos os `@android:drawable/ic_menu_*` por `@drawable/ic_*`
  - Aumentado tamanho dos ícones de 48dp para 64dp
  - Aplicado background circular em todos os ícones de categoria
- `MainActivity.java` - Adicionada lógica de navegação do Bottom Navigation
- `AcompanhamentoActivity.java` - Adicionada lógica de navegação do Bottom Navigation
- `NovoRegistroActivity.java` - Adicionada lógica de navegação do Bottom Navigation
- Todos os layouts principais convertidos para usar layout_weight para acomodar Bottom Navigation

### 📊 Metrics
- **Conformidade UI/UX:** 63% → 80-85% (comparado ao template "Saúde Transparente")
- **Ícones customizados:** 0 → 9 implementados
- **Navegação global:** 0% → 100% (todas as telas)

---

## [1.1.0] - 2026-03-22

### ✨ Added - Funcionalidades de Edição e Status

#### Sistema de Edição de Registros
- Implementado modo dual (criar/editar) em `NovoRegistroActivity.java`
  - Detecção de modo via Intent extra `"registro_para_editar"`
  - Botão "ENVIAR" muda para "ATUALIZAR" no modo edição
  - Título dinâmico "DENÚNCIA" → "EDITAR REGISTRO"
- Adicionado método `atualizarRegistro()` em `GerenciadorDados.java`
- `Registro.java` implementa `Parcelable` para passar dados entre Activities
  - Adicionados métodos setters: `setCategoria()`, `setLocal()`, `setDescricao()`, `setCaminhoImagem()`
- Botão de editar adicionado a cada item em `item_registro.xml`
- Lógica de edição implementada em `RegistroAdapter.java`
  - Click listener abre `NovoRegistroActivity` com registro via Parcelable

#### Sistema de Status Visual
- Criado enum `StatusRegistro.java` com 3 estados:
  - `PENDENTE` - Cor laranja (#FF9800)
  - `EM_ANALISE` - Cor azul (#2196F3)
  - `RESOLVIDO` - Cor verde (#4CAF50)
- Métodos do enum:
  - `getCorHex()` - Retorna código hexadecimal da cor
  - `getTexto()` - Retorna texto formatado do status
  - `proximo()` - Retorna próximo status na progressão
  - `fromString()` - Converte String para enum (compatibilidade)
- Adicionados drawables de status:
  - `bg_status_pendente.xml`
  - `bg_status_em_analise.xml`
  - `bg_status_resolvido.xml`
- Badge de status adicionado em `item_registro.xml`
  - TextView com background colorido e cantos arredondados
  - Click listener para alternar status
- Campo `status` adicionado em `Registro.java` (default: "PENDENTE")
- Lógica de mudança de status implementada em `RegistroAdapter.java`
  - Click no badge alterna status: Pendente → Em Análise → Resolvido → Pendente

### 🔧 Changed
- `Registro.java` agora implementa Parcelable
- `item_registro.xml` redesenhado com:
  - Badge de status no topo direito
  - Botão de editar acima do botão excluir
  - Data e hora em linha separada
- `RegistroAdapter.java` refatorado para suportar edição e mudança de status

---

## [1.0.0] - 2026-03-21

### 🎉 Added - Melhorias Críticas e MVP

#### Data e Hora Automática
- Timestamp automático no construtor de `Registro.java`
- Campo `data` populado com padrão "dd/MM/yyyy HH:mm"
- SimpleDateFormat aplicado no momento da criação do registro
- Exibição de data/hora em `item_registro.xml`

#### Validação Completa de Campos
- Implementada em `NovoRegistroActivity.java`:
  - ✅ Verificação de categoria selecionada (não pode estar vazia)
  - ✅ Verificação de descrição vazia
  - ✅ Validação de comprimento mínimo (10 caracteres)
  - ✅ Toast de erro específico para cada tipo de validação
  - ✅ `setError()` com foco no campo problemático

#### Persistência de Imagens
- Método `salvarImagem()` implementado em `GerenciadorDados.java`
  - Salva imagens em armazenamento interno privado
  - Compressão JPEG com qualidade 80%
  - Nome de arquivo único baseado em timestamp
  - Retorna caminho absoluto da imagem salva
- `NovoRegistroActivity.java` modificado:
  - Salva imagem antes de criar registro
  - Armazena caminho absoluto em vez de URI
  - Carrega imagem salva ao editar registro
- Campo `caminhoImagem` em `Registro.java` armazena path absoluto

#### Permissões de Galeria
- Adicionadas permissões em `AndroidManifest.xml`:
  - `READ_MEDIA_IMAGES` (Android 13+, API 33+)
  - `READ_EXTERNAL_STORAGE` (Android < 13, legacy)
- Compatibilidade com Android 7.0 (API 24) até Android 15 (API 35)

#### Otimização de Performance
- **RecyclerView otimizado** em `AcompanhamentoActivity.java`:
  - Adapter criado apenas uma vez
  - Método `atualizarDados()` implementado em `RegistroAdapter.java`
  - Evita flickering ao atualizar lista
  - Mensagem de lista vazia (`txtListaVazia`)
  - Gerenciamento de visibilidade RecyclerView vs mensagem vazia
- **Gson singleton** em `GerenciadorDados.java`:
  - Instância estática final compartilhada
  - Reduz overhead de criação do objeto
  - Melhora performance em múltiplas serializações

### 🏗️ MVP - Funcionalidades Base

#### Registro de Denúncias
- Formulário completo em `NovoRegistroActivity.java`
- 9 categorias clicáveis com seleção visual (borda verde)
- Spinner com 5 locais pré-cadastrados:
  - UBS Central
  - Hospital Regional
  - Posto de Saúde Vila Nova
  - UPA 24h Centro
  - Centro de Saúde da Família
- Campo de descrição (EditText)
- Botão de anexar foto da galeria
- Preview de imagem selecionada
- Validação antes do envio

#### Acompanhamento de Registros
- Tela de listagem em `AcompanhamentoActivity.java`
- RecyclerView com LinearLayoutManager
- Adapter customizado (`RegistroAdapter.java`)
- Layout de item (`item_registro.xml`) com:
  - CardView com elevação
  - Categoria em negrito
  - Local e data
  - Descrição truncada
  - Botão de exclusão
- Atualização automática via `onResume()`

#### Tela Inicial
- `MainActivity.java` com design limpo
- Header verde teal (#00A38D)
- Card central com:
  - Título "COMO PODEMOS AJUDAR HOJE?"
  - Botão "NOVO REGISTRO" (filled, verde)
  - Botão "VER MEUS REGISTROS" (outlined, borda verde)
- Navegação via Intents

#### Persistência de Dados
- `GerenciadorDados.java` gerencia SharedPreferences
- Chave: `"registros_saude"`
- Serialização com Gson (JSON)
- Métodos:
  - `salvarRegistros()` - Salva lista completa
  - `carregarRegistros()` - Carrega lista do SharedPreferences
  - `salvarImagem()` - Persiste imagens
  - `atualizarRegistro()` - Atualiza registro específico

#### Modelo de Dados
- `Registro.java` com campos:
  - `id` (String) - Timestamp-based unique ID
  - `categoria` (String)
  - `local` (String)
  - `descricao` (String)
  - `caminhoImagem` (String)
  - `data` (String)
  - `status` (String)
- Getters e setters completos
- Implementa Parcelable

### 🎨 Design System
- **Cores:**
  - Primary: Teal `#00A38D`
  - Background: `#F5F5F5`
  - Cards: White `#FFFFFF`
  - Text: Black `#000000`
- **Drawables:**
  - `bg_button_filled.xml` - Botões preenchidos
  - `bg_button_outline.xml` - Botões com borda
  - `bg_card_white.xml` - Cards brancos com elevação
  - `bg_header_teal.xml` - Header verde
  - `bg_status_circle.xml` - Círculo de status
  - `bg_status_circle_green.xml` - Círculo verde
- **Tema:**
  - Material Components
  - Status bar verde
  - Action bar oculta (NoActionBar)

### 📦 Dependencies
- Material Components: `1.9.0`
- Gson: `2.10.1`
- AndroidX Core KTX: `1.13.1`
- AppCompat: `1.7.0`
- ConstraintLayout: `2.1.4`
- RecyclerView: `1.3.2`

### 🔧 Configuration
- Gradle: `8.10.2`
- Android Gradle Plugin: `8.7.3`
- Kotlin: `2.0.21`
- Java: `17` (compileSdk)
- Min SDK: `24` (Android 7.0)
- Target SDK: `35` (Android 15)
- Namespace: `com.example.appmaissaude`

---

## [Unreleased] - Planejado

### 🚀 Futuras Implementações

#### Alta Prioridade
- [ ] Sistema de notificações push (Firebase Cloud Messaging)
- [ ] Tela de perfil completa (edição de dados do usuário)
- [ ] Sistema de alertas na home
- [ ] Gráficos e estatísticas (MPAndroidChart)
- [ ] Campo de busca e filtros no histórico

#### Média Prioridade
- [ ] Modo escuro (Dark Theme)
- [ ] Compartilhamento de registros
- [ ] Sistema de feedback com estrelas
- [ ] Múltiplas fotos por registro (galeria)
- [ ] Mapa interativo (Google Maps API)

#### Baixa Prioridade
- [ ] Integração com autenticação gov.br
- [ ] Sistema de chat/suporte
- [ ] Indicador de progresso visual
- [ ] Campo "Descrição Breve"
- [ ] Upload de vídeos

---

## Tipos de Mudanças

- **Added** - Novas funcionalidades
- **Changed** - Mudanças em funcionalidades existentes
- **Deprecated** - Funcionalidades que serão removidas
- **Removed** - Funcionalidades removidas
- **Fixed** - Correção de bugs
- **Security** - Correções de vulnerabilidades

---

## Links Úteis

- [Repositório GitHub](https://github.com/ClaudioMatheusDev/App_Saude_Transparente_CM)
- [Issues](https://github.com/ClaudioMatheusDev/App_Saude_Transparente_CM/issues)
- [Pull Requests](https://github.com/ClaudioMatheusDev/App_Saude_Transparente_CM/pulls)

---

**Legenda:**
- 🎉 Primeira release / MVP
- ✨ Nova funcionalidade
- 🎨 UI/UX melhorias
- 🔧 Mudanças técnicas
- 🐛 Correção de bugs
- 📊 Métricas e analytics
- 🚀 Performance
- 🔒 Segurança
- 📦 Dependências
- 🏗️ Arquitetura
