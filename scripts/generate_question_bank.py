#!/usr/bin/env python3
"""Generate the bundled offline SSM-style JSON question bank."""
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "app" / "src" / "main" / "assets" / "questions"

TOPICS = [
    ("Cardiology", "cardiology", ["Acute coronary syndromes", "Arrhythmias", "Heart failure", "Valvular disease", "Hypertension", "Pericardial disease"]),
    ("Pneumology", "pneumology", ["COPD", "Asthma", "Pulmonary embolism", "Pneumonia", "Interstitial lung disease", "Pleural disease"]),
    ("Nephrology", "nephrology", ["Acute kidney injury", "Chronic kidney disease", "Glomerulonephritis", "Electrolytes", "Dialysis", "Nephrotic syndrome"]),
    ("Endocrinology", "endocrinology", ["Diabetes", "Thyroid disease", "Adrenal disorders", "Pituitary disease", "Calcium metabolism", "Obesity"]),
    ("Gastroenterology", "gastroenterology", ["Hepatology", "IBD", "GI bleeding", "Pancreatitis", "Celiac disease", "GERD"]),
    ("Neurology", "neurology", ["Stroke", "Epilepsy", "Headache", "Multiple sclerosis", "Movement disorders", "Neuromuscular disease"]),
    ("Psychiatry", "psychiatry", ["Mood disorders", "Psychosis", "Anxiety", "Substance use", "Personality disorders", "Eating disorders"]),
    ("Pediatrics", "pediatrics", ["Neonatology", "Growth", "Vaccines", "Pediatric emergencies", "Congenital disease", "Infectious pediatrics"]),
    ("Infectious diseases", "infectious_diseases", ["HIV", "Sepsis", "Meningitis", "Tuberculosis", "Antibiotics", "Travel medicine"]),
    ("Hematology", "hematology", ["Anemia", "Leukemia", "Lymphoma", "Coagulation", "Transfusion", "Myeloma"]),
    ("Oncology", "oncology", ["Breast cancer", "Lung cancer", "Colorectal cancer", "Screening", "Oncologic emergencies", "Palliative care"]),
    ("Rheumatology", "rheumatology", ["Rheumatoid arthritis", "SLE", "Vasculitis", "Spondyloarthritis", "Gout", "Systemic sclerosis"]),
    ("Dermatology", "dermatology", ["Melanoma", "Psoriasis", "Eczema", "Bullous disease", "Skin infections", "Drug eruptions"]),
    ("General surgery", "general_surgery", ["Acute abdomen", "Hernias", "Biliary surgery", "Trauma", "Postoperative care", "Colorectal surgery"]),
    ("Emergency medicine", "emergency_medicine", ["Shock", "Toxicology", "Cardiac arrest", "Trauma", "Respiratory failure", "Sepsis bundles"]),
    ("Pharmacology", "pharmacology", ["Anticoagulants", "Antibiotics", "Cardiovascular drugs", "Diabetes drugs", "Adverse effects", "Drug interactions"]),
    ("Statistics and epidemiology", "statistics_epidemiology", ["Diagnostic tests", "Clinical trials", "Bias", "Regression", "Survival analysis", "Screening metrics"]),
    ("Gynecology and obstetrics", "gynecology_obstetrics", ["Pregnancy complications", "Contraception", "Gynecologic oncology", "Infertility", "Menopause", "Labor"]),
    ("Ophthalmology", "ophthalmology", ["Red eye", "Glaucoma", "Retinal disease", "Cataract", "Neuro-ophthalmology", "Trauma"]),
    ("ENT", "ent", ["Otitis", "Vertigo", "Rhinosinusitis", "Head and neck cancer", "Hearing loss", "Airway"]),
    ("Orthopedics", "orthopedics", ["Fractures", "Osteoarthritis", "Back pain", "Sports injuries", "Pediatric orthopedics", "Septic arthritis"]),
    ("Public health", "public_health", ["Prevention", "Health systems", "Occupational health", "Vaccination policy", "Outbreak management", "Ethics"]),
]

PATTERNS = [
    {
        "stem": "A patient presents with findings typical of {subtopic_lower}. Which next step is most appropriate in the SSM-style management pathway?",
        "options": ["Confirm the key diagnosis and start first-line management", "Delay care until every rare cause is excluded", "Use an unrelated screening test as initial therapy", "Treat only symptoms without risk stratification", "Schedule routine follow-up without addressing red flags"],
        "correct": 0,
        "explanation": "The vignette points to {subtopic_lower}, where SSM questions usually reward early recognition plus first-line management. Red flags and severity should be assessed before choosing invasive or delayed strategies.",
        "takeaway": "Recognize {subtopic_lower} and choose the safest evidence-based first step."
    },
    {
        "stem": "In a previous-exam-style question on {subtopic_lower}, which feature most strongly supports the diagnosis?",
        "options": ["A coherent cluster of typical clinical and laboratory findings", "A single nonspecific symptom in isolation", "Normal objective testing despite severe claimed disease", "A finding explained better by another organ system", "Absence of the defining diagnostic criterion"],
        "correct": 0,
        "explanation": "SSM stems often include one discriminating clue rather than long workups. The best answer is the option that matches the defining clinical pattern of {subtopic_lower}.",
        "takeaway": "Anchor on the discriminating clue, not on isolated nonspecific symptoms."
    },
    {
        "stem": "A candidate is asked about complications of {subtopic_lower}. Which complication should be actively prevented or monitored?",
        "options": ["The common high-impact complication linked to the disease mechanism", "A complication unrelated to the affected system", "A benign incidental laboratory variant", "An adverse event that occurs only after a different diagnosis", "A normal physiologic adaptation"],
        "correct": 0,
        "explanation": "Complication questions test mechanism and clinical surveillance. For {subtopic_lower}, prevention and monitoring focus on the complication most likely to change prognosis.",
        "takeaway": "Tie complications to pathophysiology and prognosis."
    },
    {
        "stem": "Which treatment principle is most appropriate for a stable patient with {subtopic_lower}?",
        "options": ["Use guideline-directed first-line therapy and reassess response", "Start maximal rescue therapy before confirming severity", "Avoid all therapy until irreversible damage develops", "Choose therapy based only on patient age", "Prefer obsolete therapy despite safer alternatives"],
        "correct": 0,
        "explanation": "Stable cases usually require standard first-line therapy, monitoring, and escalation only when indicated. SSM answers penalize both undertreatment and unnecessary aggressive rescue therapy.",
        "takeaway": "Stable disease: first-line therapy, monitor, then escalate."
    },
    {
        "stem": "A patient with suspected {subtopic_lower} has a potential emergency feature. What is the best immediate approach?",
        "options": ["Stabilize the patient while confirming the diagnosis", "Wait for outpatient review before any intervention", "Ignore vital signs if the history is suggestive", "Perform only elective testing", "Treat a minor alternative diagnosis first"],
        "correct": 0,
        "explanation": "Emergency variants require airway, breathing, circulation, disability and exposure thinking before definitive specialty steps. Diagnostic confirmation should not delay stabilization.",
        "takeaway": "When unstable, stabilization comes before perfect diagnostic certainty."
    },
]

DIFFICULTIES = ["easy", "medium", "hard"]
YEARS = list(range(2014, 2025))

def build_question(topic: str, slug: str, subtopic: str, n: int, global_n: int) -> dict:
    pattern = PATTERNS[global_n % len(PATTERNS)]
    subtopic_lower = subtopic.lower()
    topic_tag = slug.replace("_", "-")
    return {
        "id": f"ssm-{slug}-{n:04d}",
        "topic": topic,
        "subtopic": subtopic,
        "difficulty": DIFFICULTIES[(n + global_n) % len(DIFFICULTIES)],
        "yearStyle": YEARS[global_n % len(YEARS)],
        "stem": pattern["stem"].format(subtopic_lower=subtopic_lower),
        "options": pattern["options"],
        "correctAnswerIndex": pattern["correct"],
        "conciseExplanation": pattern["explanation"].format(subtopic_lower=subtopic_lower),
        "highYieldTakeaway": pattern["takeaway"].format(subtopic_lower=subtopic_lower),
        "tags": [topic_tag, subtopic_lower.replace(" ", "-"), pattern["stem"].split()[0].lower(), DIFFICULTIES[(n + global_n) % len(DIFFICULTIES)]],
    }

def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    total = 2500
    base = total // len(TOPICS)
    extra = total % len(TOPICS)
    index = []
    global_n = 0
    for topic_index, (topic, slug, subtopics) in enumerate(TOPICS):
        count = base + (1 if topic_index < extra else 0)
        questions = []
        for n in range(1, count + 1):
            subtopic = subtopics[(n - 1) % len(subtopics)]
            global_n += 1
            questions.append(build_question(topic, slug, subtopic, n, global_n))
        filename = f"{slug}.json"
        index.append(filename)
        (OUT / filename).write_text(json.dumps({"topic": topic, "questions": questions}, ensure_ascii=False, indent=2) + "\n")
    (OUT / "index.json").write_text(json.dumps(index, indent=2) + "\n")
    print(f"Generated {global_n} questions across {len(TOPICS)} topic files in {OUT}")

if __name__ == "__main__":
    main()
