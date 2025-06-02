# RemMed.
- This is an android application developed in java using android studio
- This app helps to remember medications and remedies for a medical condition
- You can add a new conditions and click on them to add medications and remedies
- you can add edit delete medications or remedies
- you can try the app from the releases
## Note
- use your own api key inside google google-services.json file
## Activities
- Main_Activity.java(Login activity)
- conditions.java(Main page of the app displays different conditions)
- meds.java(displays ore lets you update or add medications and remedies)
- Reg.java (Registering a new user)

## Firebase Firestore Structure

```bash

├── users (Collection)  
│   ├── userId_12345 (Document)   <-- Authenticated User ID  
│   │   ├── conditions (Collection)  
│   │   │   ├── Condition1 (Document)  
│   │   │   │   ├── tablets: ["Tablet 1", "Tablet 2"]  
│   │   │   │   ├── remedies: ["Remedy 1", "Remedy 2"]  
│   │   │   ├── Condition2 (Document)  
│   │   │   │   ├── tablets: ["Tablet 3"]  
│   │   │   │   ├── remedies: ["Remedy 3"]  
│   ├── userId_67890 (Document)  
│   │   ├── conditions (Collection)  
│   │   │   ├── ConditionA (Document)  
│   │   │   │   ├── tablets: ["Tablet A1", "Tablet A2"]  
│   │   │   │   ├── remedies: ["Remedy A1"]  

```

## Screenshots
<img src="https://github.com/user-attachments/assets/9e0f2789-ffe2-40e5-80e8-a916452d163b" width="200" />
<img src="https://github.com/user-attachments/assets/5b3817c4-19ab-4ae3-93a4-1d15292ef99b" width="200" />
<img src="https://github.com/user-attachments/assets/7dff8244-0adf-4e19-b58a-5dc1d246ed7a" width="200" />
<img src="https://github.com/user-attachments/assets/48aa8101-cae4-480c-a74d-c0595f4e9f13" width="200" />

## Design
<img src="https://github.com/user-attachments/assets/2f24ccbb-34fb-4dec-8728-960b915a0094"/>

