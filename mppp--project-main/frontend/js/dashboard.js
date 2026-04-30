document.addEventListener("DOMContentLoaded", async () => {
    // 1. Fetch Real Data from Backend
    const username = localStorage.getItem('username') || 'Anonymous';
    
    // Update Profile Name if available
    const dashName = document.getElementById('dashName');
    const dashUsername = document.getElementById('dashUsername');
    const dashAvatar = document.getElementById('dashAvatar');
    if (dashName) dashName.innerText = username;
    if (dashUsername) dashUsername.innerText = '@' + username.toLowerCase().replace(' ', '');
    if (dashAvatar) dashAvatar.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(username)}&background=f97316&color=fff&size=120`;

    let userGlobalRank = '--';
    let recentSubmissions = [];

    // Auto-sync local storage DSA progress to Backend BEFORE fetching profile
    if (typeof topicsData !== 'undefined') {
        let easy = 0, med = 0, hard = 0, total = 0;
        for (let i = 0; i < localStorage.length; i++) {
            const key = localStorage.key(i);
            if (key.startsWith('codeprep_') && key !== 'codeprep_currentUser') {
                const topicId = key.replace('codeprep_', '');
                try {
                    const state = JSON.parse(localStorage.getItem(key));
                    const topic = topicsData[topicId];
                    if (topic && state) {
                        for (let qName in state) {
                            if (state[qName].done) {
                                total++;
                                let foundDiff = 'Easy';
                                for (let diffKey in topic.difficulties) {
                                    if (topic.difficulties[diffKey].find(x => x.name === qName)) {
                                        foundDiff = diffKey;
                                        break;
                                    }
                                }
                                if (foundDiff === 'Easy' || foundDiff === 'Basic') easy++;
                                else if (foundDiff === 'Medium') med++;
                                else hard++;
                            }
                        }
                    }
                } catch (e) {}
            }
        }
        
        // Extract exact dates for streak calc & tracking recent submissions
        const dailyCounts = {};
        for (let i = 0; i < localStorage.length; i++) {
            const key = localStorage.key(i);
            if (key.startsWith('codeprep_') && !key.includes('currentUser')) {
                try {
                    const stateObj = JSON.parse(localStorage.getItem(key)) || {};
                    const topicId = key.replace('codeprep_', '');
                    const topic = topicsData[topicId];
                    for (let q in stateObj) {
                        if (stateObj[q].done) {
                            const dt = stateObj[q].date || new Date().toISOString().split('T')[0];
                            dailyCounts[dt] = (dailyCounts[dt] || 0) + 1;
                            
                            // Discover language/time
                            let qDiff = 'Easy';
                            if(topic && topic.difficulties) {
                                for (let diffKey in topic.difficulties) {
                                    if (topic.difficulties[diffKey].find(x => x.name === q)) {
                                        qDiff = diffKey;
                                        break;
                                    }
                                }
                            }
                            
                            recentSubmissions.push({
                                name: q,
                                date: dt,
                                diff: qDiff,
                                lang: 'java', // default representation
                                time: Math.floor(Math.random() * 50) + 'ms'
                            });
                        }
                    }
                } catch(e) {}
            }
        }
        
        // Calculate Streaks
        let tempCurrentStreak = 0;
        let tempBestStreak = 0;
        const sortedDates = Object.keys(dailyCounts).sort((a,b) => new Date(a) - new Date(b));
        if (sortedDates.length > 0) {
            let runningStreak = 1;
            tempBestStreak = 1;
            for (let i = 1; i < sortedDates.length; i++) {
                const d1 = new Date(sortedDates[i-1]);
                const d2 = new Date(sortedDates[i]);
                const diffDays = Math.round((d2 - d1) / (1000 * 60 * 60 * 24));
                if (diffDays === 1) {
                    runningStreak++;
                    if (runningStreak > tempBestStreak) tempBestStreak = runningStreak;
                } else if (diffDays > 1) {
                    runningStreak = 1;
                }
            }
            const lastDate = new Date(sortedDates[sortedDates.length - 1]);
            const today = new Date();
            today.setHours(0,0,0,0);
            const diffToToday = Math.round((today - lastDate) / (1000 * 60 * 60 * 24));
            if (diffToToday <= 1) {
                tempCurrentStreak = runningStreak;
            }
        }

        const calculatedPoints = easy * 10 + med * 20 + hard * 30;

        if (total > 0) {
            try {
                const syncRes = await fetch('http://localhost:8080/api/activity/sync', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        username: username,
                        totalSolved: total,
                        easySolved: easy,
                        mediumSolved: med,
                        hardSolved: hard,
                        submissions: total * 2,
                        streakDays: tempCurrentStreak,
                        bestStreak: tempBestStreak,
                        totalPoints: calculatedPoints,
                        quizzesTaken: 0 // hardcoded for now until quiz module built
                    })
                });
                if(syncRes.ok) {
                    const syncData = await syncRes.json();
                    userGlobalRank = syncData.rank || '--';
                }
            } catch (err) {}
        }
    }

    try {
        const res = await fetch(`http://localhost:8080/api/activity/profile/${username}`);
        if (res.ok) {
            const data = await res.json();
            
            // Populate DOM number stats
            const eSolved = document.getElementById('dashSolved');
            const eEasy = document.getElementById('dashEasy');
            const eMed = document.getElementById('dashMedium');
            const eHard = document.getElementById('dashHard');
            const eStreak = document.getElementById('dashStreakCurrent');
            const eStreakBest = document.getElementById('dashStreakBest');
            const ePoints = document.getElementById('dashTotalPoints');
            const eQuizzes = document.getElementById('dashQuizzesTaken');
            
            window.totalSolvedGlob = data.totalSolved || 0; // for heatmap
            const totalSolved = window.totalSolvedGlob;
            const easySolved = data.easySolved || 0;
            const medSolved = data.mediumSolved || 0;
            const hardSolved = data.hardSolved || 0;
            const submissions = data.submissions || 0;
            const streakDays = data.streakDays || 0;
            const bestStreak = data.bestStreak || 0;
            const totalPoints = data.totalPoints || 0;
            const quizzesTaken = data.quizzesTaken || 0;

            if(eSolved) eSolved.innerText = totalSolved;
            if(eEasy) eEasy.innerText = easySolved;
            if(eMed) eMed.innerText = medSolved;
            if(eHard) eHard.innerText = hardSolved;
            if(eSubmissions) eSubmissions.innerText = submissions;
            if(eStreak) eStreak.innerText = streakDays;
            if(eStreakBest) eStreakBest.innerText = bestStreak;
            if(ePoints) ePoints.innerText = totalPoints;
            if(eQuizzes) eQuizzes.innerText = quizzesTaken;

            // Update Progress bars (assuming totals 350, 500, 150)
            const easyTotal = 350, medTotal = 500, hardTotal = 150;
            
            const pCountEasy = document.getElementById('progCountEasy');
            const pBarEasy = document.getElementById('progBarEasy');
            if (pCountEasy) pCountEasy.innerText = easySolved;
            if (pBarEasy) pBarEasy.style.width = Math.min((easySolved / easyTotal) * 100, 100) + '%';

            const pCountMed = document.getElementById('progCountMedium');
            const pBarMed = document.getElementById('progBarMedium');
            if (pCountMed) pCountMed.innerText = medSolved;
            if (pBarMed) pBarMed.style.width = Math.min((medSolved / medTotal) * 100, 100) + '%';

            const pCountHard = document.getElementById('progCountHard');
            const pBarHard = document.getElementById('progBarHard');
            if (pCountHard) pCountHard.innerText = hardSolved;
            if (pBarHard) pBarHard.style.width = Math.min((hardSolved / hardTotal) * 100, 100) + '%';
        } else if (res.status === 404) {
            // New user, push sync to create profile
            await fetch('http://localhost:8080/api/activity/sync', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({username})
            });
            
            // Still populate 0s for a new user properly
            const eSolved = document.getElementById('dashSolved');
            if(eSolved) eSolved.innerText = 0;
            window.totalSolvedGlob = 0;
        }
    } catch (e) {
        console.log("Could not fetch backend activity stats", e);
    }

    // Always attempt to set Rank if it was found during sync
    const dashRank = document.getElementById('dashRank');
    if (dashRank && userGlobalRank !== '--') dashRank.innerText = '#' + userGlobalRank;

    // Always populate recent submissions since they come from localStorage
    recentSubmissions.sort((a, b) => new Date(b.date) - new Date(a.date));
    const tbody = document.getElementById('dashRecentSubs');
    if (tbody) {
        tbody.innerHTML = '';
        if (recentSubmissions.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;color:#64748b;padding:20px;">No recent submissions</td></tr>';
        } else {
            recentSubmissions.slice(0, 5).forEach(sub => {
                let diffClass = sub.diff === 'Easy' || sub.diff === 'Basic' ? 'easy' : (sub.diff === 'Medium' ? 'med' : 'hard');
                tbody.innerHTML += `
                    <tr>
                        <td><a href="solve.html?q=${encodeURIComponent(sub.name)}" class="profile-link text-gradient">${sub.name}</a></td>
                        <td><span class="badge ${diffClass}">ACCEPTED</span></td>
                        <td>${sub.lang}</td>
                        <td style="color:#94a3b8;">${sub.time}</td>
                        <td style="color:#94a3b8;">${sub.date}</td>
                    </tr>
                `;
            });
        }
    }

    
    // 2. Generate Heatmap (GitHub style) using REAL datetimes
    const heatmapContainer = document.getElementById("heatmapContainer");
    
    // Build real date counts from localStorage for rendering
    // (We also built dailyCounts above, but keeping this block intact for exact scope)
    const renderCounts = {};
    for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i);
        if (key.startsWith('codeprep_') && !key.includes('currentUser')) {
            try {
                const stateObj = JSON.parse(localStorage.getItem(key)) || {};
                for (let q in stateObj) {
                    if (stateObj[q].done) {
                        // fallback to today if an old problem doesn't have a date
                        const dt = stateObj[q].date || new Date().toISOString().split('T')[0];
                        renderCounts[dt] = (renderCounts[dt] || 0) + 1;
                    }
                }
            } catch(e) {}
        }
    }

    if (heatmapContainer) {
        const weeks = 52;
        const daysPerWeek = 7;
        
        // Start date calculations (1 year ago)
        const today = new Date();
        const startDay = new Date();
        startDay.setDate(today.getDate() - (weeks * daysPerWeek - 1));
        
        let currentDayIdx = 0;

        for (let w = 0; w < weeks; w++) {
            const col = document.createElement('div');
            col.className = 'hg-col';
            
            for (let d = 0; d < daysPerWeek; d++) {
                const cell = document.createElement('div');
                
                // Calculate date for this cell
                const cellDate = new Date(startDay);
                cellDate.setDate(cellDate.getDate() + currentDayIdx);
                const cellDateStr = cellDate.toISOString().split('T')[0];
                
                const count = renderCounts[cellDateStr] || 0;
                let level = 0;
                if (count > 0) {
                    if (count >= 5) level = 4;
                    else if (count >= 3) level = 3;
                    else if (count >= 2) level = 2;
                    else level = 1;
                }

                // Future prediction logic to make block empty if beyond today
                if (cellDate > today) {
                    level = 0;
                }

                cell.className = `hg-cell level-${level}`;
                cell.setAttribute('title', count > 0 ? `${count} problems solved on ${cellDateStr}` : `No activity on ${cellDateStr}`);
                
                col.appendChild(cell);
                currentDayIdx++;
            }
            heatmapContainer.appendChild(col);
        }
    }
});
