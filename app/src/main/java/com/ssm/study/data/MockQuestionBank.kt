package com.ssm.study.data

object MockQuestionBank {
    val questions = listOf(
        QuestionEntity(
            id = "ssm-2023-cardio-001",
            topic = Topic.CARDIOLOGY,
            year = 2023,
            stem = "A 68-year-old with crushing chest pain has ST elevation in II, III and aVF. Which artery is most often occluded?",
            optionA = "Left anterior descending artery",
            optionB = "Right coronary artery",
            optionC = "Left circumflex artery",
            optionD = "Left main coronary artery",
            optionE = "Posterior descending vein",
            correctIndex = 1,
            explanation = "Inferior STEMI classically involves leads II, III and aVF. The right coronary artery is the culprit in most right-dominant circulations.",
            takeaway = "Inferior STEMI = think RCA first."
        ),
        QuestionEntity(
            id = "ssm-2022-pneumo-001",
            topic = Topic.PNEUMOLOGY,
            year = 2022,
            stem = "A smoker has chronic cough, hyperinflation and reduced FEV1/FVC. Which spirometric pattern is expected?",
            optionA = "Restrictive pattern with high FEV1/FVC",
            optionB = "Normal spirometry",
            optionC = "Obstructive pattern with low FEV1/FVC",
            optionD = "Isolated low residual volume",
            optionE = "Increased FEV1 with bronchodilator only",
            correctIndex = 2,
            explanation = "COPD causes expiratory airflow limitation. Spirometry shows a reduced FEV1/FVC ratio, usually with incomplete reversibility.",
            takeaway = "COPD diagnosis requires obstruction on spirometry."
        ),
        QuestionEntity(
            id = "ssm-2021-nephro-001",
            topic = Topic.NEPHROLOGY,
            year = 2021,
            stem = "Severe hyperkalemia with ECG changes is found in the emergency department. What is the first treatment?",
            optionA = "Oral potassium binder",
            optionB = "IV calcium gluconate",
            optionC = "Loop diuretic only",
            optionD = "Insulin after four hours",
            optionE = "Fluid restriction",
            correctIndex = 1,
            explanation = "ECG changes make hyperkalemia immediately life-threatening. IV calcium stabilizes the myocardium while potassium-shifting and elimination therapies are arranged.",
            takeaway = "Hyperkalemia + ECG changes: calcium first."
        ),
        QuestionEntity(
            id = "ssm-2020-endo-001",
            topic = Topic.ENDOCRINOLOGY,
            year = 2020,
            stem = "A patient with weight loss, tremor and low TSH has diffuse uptake on thyroid scintigraphy. Most likely diagnosis?",
            optionA = "Graves disease",
            optionB = "Subacute thyroiditis",
            optionC = "Toxic adenoma",
            optionD = "Central hypothyroidism",
            optionE = "Iodine deficiency",
            correctIndex = 0,
            explanation = "Diffuse increased uptake points to Graves disease. Thyroiditis typically has low uptake because hormone leaks from an inflamed gland.",
            takeaway = "Diffuse uptake in thyrotoxicosis suggests Graves."
        ),
        QuestionEntity(
            id = "ssm-2024-neuro-001",
            topic = Topic.NEUROLOGY,
            year = 2024,
            stem = "Sudden aphasia and right arm weakness began 90 minutes ago. CT excludes hemorrhage. What is the key next step if eligible?",
            optionA = "Aspirin only after 48 hours",
            optionB = "IV thrombolysis assessment",
            optionC = "Routine EEG",
            optionD = "Lumbar puncture before treatment",
            optionE = "Discharge with outpatient MRI",
            correctIndex = 1,
            explanation = "Acute ischemic stroke within the treatment window requires rapid reperfusion evaluation. Non-contrast CT first excludes hemorrhage.",
            takeaway = "Time is brain: consider reperfusion early."
        ),
        QuestionEntity(
            id = "ssm-2022-stat-001",
            topic = Topic.STATISTICS,
            year = 2022,
            stem = "In a screening test, which measure increases when disease prevalence increases?",
            optionA = "Sensitivity",
            optionB = "Specificity",
            optionC = "Positive predictive value",
            optionD = "Likelihood ratio negative",
            optionE = "Area under ROC curve",
            correctIndex = 2,
            explanation = "Predictive values depend on prevalence. As prevalence rises, positive results are more likely to be true positives, increasing PPV.",
            takeaway = "Prevalence changes predictive values, not sensitivity/specificity."
        )
    )
}
