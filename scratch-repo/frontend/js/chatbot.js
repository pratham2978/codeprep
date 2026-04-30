/**
 * CodePrep AI Chatbot Widget
 * Floating chatbot bubble that calls the /api/chat endpoint (Gemini)
 * Inject this into any page via: <script src="js/chatbot.js"></script>
 */
(function () {
    const API_URL = 'http://localhost:8080/api/chat';

    // --- Inject styles ---
    const style = document.createElement('style');
    style.textContent = `
        #cp-chatbot-bubble {
            position: fixed; bottom: 28px; right: 28px; z-index: 9999;
            width: 56px; height: 56px; border-radius: 50%;
            background: linear-gradient(135deg, #ff7300, #fbbf24);
            cursor: pointer; display: flex; align-items: center; justify-content: center;
            font-size: 1.4rem; box-shadow: 0 6px 24px rgba(255,115,0,0.45);
            transition: transform 0.25s, box-shadow 0.25s;
            border: none; color: #000;
        }
        #cp-chatbot-bubble:hover { transform: scale(1.1); box-shadow: 0 10px 30px rgba(255,115,0,0.55); }
        #cp-chatbot-bubble .notif-dot {
            position: absolute; top: 2px; right: 2px; width: 12px; height: 12px;
            background: #ef4444; border-radius: 50%; border: 2px solid #0f111a;
            animation: pulse-dot 2s infinite;
        }
        @keyframes pulse-dot { 0%,100%{opacity:1;transform:scale(1)} 50%{opacity:0.6;transform:scale(1.3)} }

        #cp-chatbot-panel {
            position: fixed; bottom: 96px; right: 28px; z-index: 9998;
            width: 380px; max-height: 560px;
            background: #1a1d2d; border: 1px solid rgba(255,255,255,0.1);
            border-radius: 20px; display: flex; flex-direction: column;
            box-shadow: 0 20px 60px rgba(0,0,0,0.7);
            transform: scale(0.9) translateY(20px); opacity: 0;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            pointer-events: none;
        }
        #cp-chatbot-panel.open {
            transform: scale(1) translateY(0); opacity: 1; pointer-events: all;
        }

        .cp-chat-header {
            padding: 16px 20px; border-bottom: 1px solid rgba(255,255,255,0.08);
            display: flex; align-items: center; gap: 12px;
            background: linear-gradient(135deg, rgba(255,115,0,0.1), transparent);
            border-radius: 20px 20px 0 0;
        }
        .cp-chat-avatar {
            width: 38px; height: 38px; border-radius: 50%;
            background: linear-gradient(135deg, #ff7300, #fbbf24);
            display: flex; align-items: center; justify-content: center;
            font-size: 1.1rem; font-weight: 800; color: #000; flex-shrink: 0;
        }
        .cp-chat-info { flex: 1; }
        .cp-chat-info h4 { margin: 0; font-size: 0.95rem; font-weight: 700; }
        .cp-chat-info p { margin: 0; font-size: 0.75rem; color: #10b981; display: flex; align-items: center; gap: 4px; }
        .cp-close-btn {
            background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1);
            color: #fff; width: 30px; height: 30px; border-radius: 8px;
            cursor: pointer; font-size: 0.9rem; display: flex; align-items: center; justify-content: center;
        }

        .cp-messages {
            flex: 1; overflow-y: auto; padding: 16px;
            display: flex; flex-direction: column; gap: 12px;
            scrollbar-width: thin; scrollbar-color: rgba(255,255,255,0.08) transparent;
        }

        .cp-msg { max-width: 85%; padding: 10px 14px; border-radius: 12px; font-size: 0.9rem; line-height: 1.5; }
        .cp-msg.bot {
            background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.07);
            color: #cbd5e1; border-radius: 4px 12px 12px 12px; align-self: flex-start;
        }
        .cp-msg.user {
            background: linear-gradient(135deg, rgba(255,115,0,0.2), rgba(251,191,36,0.12));
            border: 1px solid rgba(255,115,0,0.25);
            color: #fff; border-radius: 12px 4px 12px 12px; align-self: flex-end;
        }
        .cp-msg.loading { animation: cpPulse 1.2s infinite; }
        @keyframes cpPulse { 0%,100%{opacity:0.5} 50%{opacity:1} }

        .cp-quick-btns {
            display: flex; gap: 6px; flex-wrap: wrap; padding: 0 16px 8px;
        }
        .cp-quick-btn {
            padding: 5px 12px; border-radius: 20px;
            background: rgba(255,115,0,0.08); border: 1px solid rgba(255,115,0,0.2);
            color: #ff983f; font-size: 0.75rem; font-weight: 600; cursor: pointer; transition: 0.2s;
        }
        .cp-quick-btn:hover { background: rgba(255,115,0,0.18); }

        .cp-input-row {
            padding: 12px 16px; border-top: 1px solid rgba(255,255,255,0.07);
            display: flex; gap: 8px; border-radius: 0 0 20px 20px;
        }
        .cp-input {
            flex: 1; background: rgba(255,255,255,0.05); border: 1px solid rgba(255,255,255,0.1);
            border-radius: 10px; padding: 10px 14px; color: #fff;
            font-family: 'Inter', sans-serif; font-size: 0.9rem;
        }
        .cp-input:focus { outline: none; border-color: rgba(255,115,0,0.4); }
        .cp-send-btn {
            width: 40px; height: 40px; border-radius: 10px;
            background: linear-gradient(135deg, #ff7300, #fbbf24);
            border: none; color: #000; cursor: pointer; font-size: 1rem;
            display: flex; align-items: center; justify-content: center;
            transition: 0.2s;
        }
        .cp-send-btn:hover { transform: scale(1.08); }
        .cp-send-btn:disabled { opacity: 0.5; cursor: not-allowed; transform: none; }

        @media(max-width: 480px) {
            #cp-chatbot-panel { width: calc(100vw - 20px); right: 10px; bottom: 80px; }
        }
    `;
    document.head.appendChild(style);

    // --- Build HTML ---
    const bubble = document.createElement('button');
    bubble.id = 'cp-chatbot-bubble';
    bubble.innerHTML = `<i class="fa-solid fa-robot"></i><span class="notif-dot"></span>`;
    bubble.title = 'Ask CodePrep AI';

    const panel = document.createElement('div');
    panel.id = 'cp-chatbot-panel';
    panel.innerHTML = `
        <div class="cp-chat-header">
            <div class="cp-chat-avatar">AI</div>
            <div class="cp-info">
                <h4>CodePrep AI Assistant</h4>
                <p><span style="width:7px;height:7px;background:#10b981;border-radius:50%;display:inline-block;"></span> Online · Powered by Gemini</p>
            </div>
            <button class="cp-close-btn" id="cp-close">✕</button>
        </div>
        <div class="cp-messages" id="cp-messages">
            <div class="cp-msg bot">👋 Hi! I'm your CodePrep AI. Ask me about any DSA problem, algorithm, time complexity, or coding concept!</div>
        </div>
        <div class="cp-quick-btns">
            <button class="cp-quick-btn" onclick="cpAsk('Explain Two Pointers technique')">Two Pointers</button>
            <button class="cp-quick-btn" onclick="cpAsk('What is dynamic programming?')">Dynamic Programming</button>
            <button class="cp-quick-btn" onclick="cpAsk('Big O notation explained')">Big O</button>
        </div>
        <div class="cp-input-row">
            <input class="cp-input" id="cp-input" placeholder="Ask anything about DSA..." />
            <button class="cp-send-btn" id="cp-send" onclick="cpSend()"><i class="fa-solid fa-paper-plane"></i></button>
        </div>
    `;

    document.body.appendChild(bubble);
    document.body.appendChild(panel);

    // --- State ---
    let isOpen = false;
    let isLoading = false;
    const conversationHistory = [];

    // --- Toggle ---
    bubble.addEventListener('click', () => {
        isOpen = !isOpen;
        panel.classList.toggle('open', isOpen);
        bubble.querySelector('.notif-dot')?.remove();
        if (isOpen) document.getElementById('cp-input')?.focus();
    });

    document.getElementById('cp-close').addEventListener('click', () => {
        isOpen = false;
        panel.classList.remove('open');
    });

    // --- Enter key ---
    document.getElementById('cp-input').addEventListener('keydown', (e) => {
        if (e.key === 'Enter') cpSend();
    });

    // --- Send message ---
    window.cpAsk = function (text) {
        const input = document.getElementById('cp-input');
        input.value = text;
        cpSend();
    };

    window.cpSend = async function () {
        if (isLoading) return;
        const input = document.getElementById('cp-input');
        const msg = input.value.trim();
        if (!msg) return;

        isLoading = true;
        input.value = '';
        document.getElementById('cp-send').disabled = true;

        appendMsg(msg, 'user');
        conversationHistory.push({ role: 'user', text: msg });

        const loadingEl = appendMsg('Thinking...', 'bot loading');

        try {
            const systemPrompt = `You are CodePrep AI, a friendly and expert DSA & competitive programming tutor. 
Answer concisely and clearly. Use simple examples when explaining algorithms. 
Format code snippets with proper indentation. Keep answers under 150 words unless the user asks for more detail.
Context from conversation so far: ${conversationHistory.slice(-4).map(h => h.role + ': ' + h.text).join(' | ')}
User's current question: ${msg}`;

            const res = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: systemPrompt })
            });

            const data = await res.json();
            loadingEl.textContent = data.reply || 'Sorry, something went wrong.';
            loadingEl.classList.remove('loading');
            conversationHistory.push({ role: 'bot', text: data.reply });
        } catch (e) {
            loadingEl.textContent = '⚠️ Could not reach the backend. Make sure Spring Boot is running on port 8080.';
            loadingEl.classList.remove('loading');
        }

        isLoading = false;
        document.getElementById('cp-send').disabled = false;
        scrollToBottom();
    };

    function appendMsg(text, classes) {
        const msgs = document.getElementById('cp-messages');
        const div = document.createElement('div');
        div.className = `cp-msg ${classes}`;
        div.textContent = text;
        msgs.appendChild(div);
        scrollToBottom();
        return div;
    }

    function scrollToBottom() {
        const msgs = document.getElementById('cp-messages');
        msgs.scrollTop = msgs.scrollHeight;
    }
})();
