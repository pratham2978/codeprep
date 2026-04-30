document.addEventListener("DOMContentLoaded", () => {
    // Register GSAP config to ignore missing targets if elements aren't rendered
    gsap.config({ nullTargetWarn: false });

    // ==========================================
    // 1. INITIAL SETUP
    // ==========================================
    const masterTl = gsap.timeline();

    gsap.set(".gsap-nav, .gsap-fade-up", { y: 30, opacity: 0 });
    gsap.set(".ide-window", { scale: 0.8, rotationX: 20, rotationY: -20, opacity: 0, z: -200 });
    gsap.set(".float-card", { scale: 0, opacity: 0, rotation: -15 });

    // ==========================================
    // 2. MAIN REVEAL ANIMATION
    // ==========================================
    masterTl
        .to(".gsap-nav", { y: 0, opacity: 1, duration: 1, ease: "power3.out" })
        .to(".gsap-fade-up", { 
            y: 0, 
            opacity: 1, 
            duration: 0.8, 
            stagger: 0.1, 
            ease: "power2.out" 
        }, "-=0.6")
        .to(".ide-window", {
            scale: 1,
            rotationX: 0,
            rotationY: 0,
            z: 0,
            opacity: 1,
            duration: 1.2,
            ease: "back.out(1.5)"
        }, "-=0.8")
        .to(".float-card", {
            scale: 1,
            opacity: 1,
            rotation: 0,
            duration: 1,
            stagger: 0.15,
            ease: "elastic.out(1, 0.6)"
        }, "-=0.8");

    // ==========================================
    // 3. AFTER REVEAL: FLOATING & CODE TYPING
    // ==========================================
    masterTl.eventCallback("onComplete", () => {
        
        // --- A. Organic Continuous Floating ---
        const floatingElements = document.querySelectorAll(".parallax");
        floatingElements.forEach((el) => {
            gsap.to(el, {
                y: `+=${15 + Math.random() * 15}`,
                x: `+=${-10 + Math.random() * 20}`,
                rotationZ: `-=${2 - Math.random() * 4}`,
                duration: 3 + Math.random() * 2,
                ease: "sine.inOut",
                yoyo: true,
                repeat: -1,
                delay: Math.random() * 2
            });
        });

        // --- B. Dynamic C++ Code Reveal Animation ---
        const codeLines = [
            `<span class="syntax-keyword">class</span> <span class="syntax-type">Solution</span> <span class="syntax-bracket">{</span>`,
            `<span class="syntax-keyword">public:</span>`,
            `    <span class="syntax-type">vector</span>&lt;<span class="syntax-keyword">int</span>&gt; <span class="syntax-func">twoSum</span>(<span class="syntax-type">vector</span>&lt;<span class="syntax-keyword">int</span>&gt;&amp; <span class="syntax-var">nums</span>, <span class="syntax-keyword">int</span> <span class="syntax-var">target</span>) <span class="syntax-bracket">{</span>`,
            `        <span class="syntax-type">unordered_map</span>&lt;<span class="syntax-keyword">int</span>, <span class="syntax-keyword">int</span>&gt; <span class="syntax-var">map</span>;`,
            `        <span class="syntax-keyword">for</span> (<span class="syntax-keyword">int</span> <span class="syntax-var">i</span> = 0; i &lt; nums.size(); i++) <span class="syntax-bracket">{</span>`,
            `            <span class="syntax-keyword">int</span> <span class="syntax-var">comp</span> = target - nums[i];`,
            `            <span class="syntax-keyword">if</span> (map.count(comp)) <span class="syntax-keyword">return</span> <span class="syntax-bracket">{</span>map[comp], i<span class="syntax-bracket">}</span>;`,
            `            map[nums[i]] = i;`,
            `        <span class="syntax-bracket">}</span>`,
            `        <span class="syntax-keyword">return</span> <span class="syntax-bracket">{}</span>;`,
            `    <span class="syntax-bracket">}</span>`,
            `<span class="syntax-bracket">}</span>;`
        ];

        const codeContainer = document.getElementById("code-container");
        
        // Inject the lines into the DOM securely wrapped
        codeLines.forEach(line => {
            const lineDiv = document.createElement("div");
            lineDiv.className = "code-line-wrap";
            // Important: Use innerHTML here because the array contains raw HTML spans
            lineDiv.innerHTML = `<div class="code-line-content" style="opacity:0; transform:translateY(20px);">${line}</div>`;
            codeContainer.appendChild(lineDiv);
        });

        // Create animation timeline for the code
        const codeTl = gsap.timeline({ delay: 0.2 });

        // Slide each line up into view
        codeTl.to(".code-line-content", { 
            y: 0, 
            opacity: 1, 
            duration: 0.3, 
            stagger: 0.1, 
            ease: "power2.out" 
        })
        // Pop the success box
        .to("#success-box", {
            opacity: 1,
            y: 0,
            duration: 0.6,
            ease: "back.out(1.5)"
        }, "+=0.2");
    });

    // ==========================================
    // 4. HIGH-PERFORMANCE 3D MOUSE PARALLAX
    // ==========================================
    const scene = document.getElementById("scene");
    const parallaxLayers = document.querySelectorAll(".parallax");

    if (window.innerWidth >= 1024 && scene) {
        // gsap.quickTo is optimized for mouse tracking (no lag)
        const setX = gsap.quickTo(parallaxLayers, "x", { duration: 0.4, ease: "power2.out" });
        const setY = gsap.quickTo(parallaxLayers, "y", { duration: 0.4, ease: "power2.out" });
        const setRotX = gsap.quickTo(parallaxLayers, "rotationX", { duration: 0.4, ease: "power2.out" });
        const setRotY = gsap.quickTo(parallaxLayers, "rotationY", { duration: 0.4, ease: "power2.out" });

        scene.addEventListener("mousemove", (e) => {
            const rect = scene.getBoundingClientRect();
            // Normalize -1 to 1
            const normX = ((e.clientX - rect.left) / rect.width) * 2 - 1;
            const normY = ((e.clientY - rect.top) / rect.height) * 2 - 1;

            parallaxLayers.forEach((layer) => {
                const depth = parseFloat(layer.getAttribute("data-depth"));
                const rotateMultiplier = parseFloat(layer.getAttribute("data-rotate"));

                gsap.to(layer, {
                    x: normX * 100 * depth,
                    y: normY * 100 * depth,
                    rotationX: -normY * 40 * rotateMultiplier,
                    rotationY: normX * 40 * rotateMultiplier,
                    duration: 0.5,
                    ease: "power2.out",
                    overwrite: "auto"
                });
            });
        });

        scene.addEventListener("mouseleave", () => {
            gsap.to(parallaxLayers, {
                x: 0,
                y: 0,
                rotationX: 0,
                rotationY: 0,
                duration: 1.2,
                ease: "elastic.out(1, 0.3)"
            });
        });
    }
});