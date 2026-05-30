# 🅿️ PARKING LOT SYSTEM - LLD DIAGRAM & INTERVIEW NOTES

## 🎯 SYSTEM OVERVIEW
```
Vehicle → EntranceGate → ParkingBuilding → ParkingLevel → ParkingSpotManager → ParkingSpot
                                      ↓
                               Generate Ticket
                                      ↓  
ExitGate ← Payment ← CostComputation ← Ticket (contains vehicle, spot, level, entry time)
```

---

## 📊 MAIN ARCHITECTURE DIAGRAM

```
┌─────────────────────────────────────────────────────────────────┐
│                          PARKING LOT                             │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐    ┌─────────────────────────────────────┐  │
│  │  ENTRANCE GATE  │    │           PARKING BUILDING          │  │
│  │                 │    │  ┌─────────────────────────────────┐ │  │
│  │  - enter()      │───▶│  │         PARKING LEVEL 1         │ │  │
│  └─────────────────┘    │  │  ┌─────────────────────────────┐ │ │  │
│                         │  │  │    SPOT MANAGERS           │ │ │  │
│  ┌─────────────────┐    │  │  │  ┌───────────────────────┐ │ │ │  │
│  │    EXIT GATE    │    │  │  │  │  TwoWheelerSpotMgr   │ │ │ │  │
│  │                 │    │  │  │  │  ┌─────────────────┐ │ │ │ │  │
│  │  - completeExit()◀───┤  │  │  │  │  ParkingSpots   │ │ │ │ │  │
│  │  - calculatePrice() │  │  │  │  └─────────────────┘ │ │ │ │  │
│  └─────────────────┘    │  │  │  └───────────────────────┘ │ │ │  │
│                         │  │  │  ┌───────────────────────┐ │ │ │  │
│                         │  │  │  │  FourWheelerSpotMgr  │ │ │ │  │
│                         │  │  │  │  ┌─────────────────┐ │ │ │ │  │
│                         │  │  │  │  │  ParkingSpots   │ │ │ │ │  │
│                         │  │  │  │  └─────────────────┘ │ │ │ │  │
│                         │  │  │  └───────────────────────┘ │ │ │  │
│                         │  │  └─────────────────────────────┘ │ │  │
│                         │  └─────────────────────────────────┘ │  │
│                         │             LEVEL 2, 3, ...          │  │
│                         └─────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🏗️ CLASS RELATIONSHIP DIAGRAM

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│     VEHICLE     │     │     TICKET      │     │  PARKING SPOT   │
│─────────────────│     │─────────────────│     │─────────────────│
│-vehicleNumber   │────▶│-vehicle         │────▶│-spotId          │
│-vehicleType     │     │-level           │     │-isFree          │
│                 │     │-spot            │     │                 │
│+getVehicleType()│     │-entryTime       │     │+occupySpot()    │
└─────────────────┘     └─────────────────┘     │+releaseSpot()   │
                                                └─────────────────┘
        │                        │                        ▲
        │                        │                        │
        ▼                        ▼                        │
┌─────────────────┐     ┌─────────────────┐              │
│  VEHICLE TYPE   │     │ PARKING LEVEL   │              │
│─────────────────│     │─────────────────│              │
│TWO_WHEELER      │     │-levelNumber     │              │
│FOUR_WHEELER     │     │-spotManagers    │              │
└─────────────────┘     │                 │              │
                        │+hasAvailability()│──────────────┘
                        │+park()          │
                        │+unPark()        │
                        └─────────────────┘
                                │
                                ▼
                   ┌─────────────────────────┐
                   │  PARKING SPOT MANAGER   │
                   │─────────────────────────│
                   │-spots: List<ParkingSpot>│
                   │-strategy               │
                   │-lock: ReentrantLock    │
                   │                        │
                   │+park(): ParkingSpot    │
                   │+unPark(spot)           │
                   │+hasFreeSpot(): boolean │
                   └─────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────────────────┐
        │            STRATEGY PATTERN                     │
        │┌─────────────────────────────────────────────┐ │
        ││        ParkingSpotLookupStrategy            │ │
        ││─────────────────────────────────────────────│ │
        ││+selectSpot(spots): ParkingSpot              │ │
        │└─────────────────────────────────────────────┘ │
        │                      ▲                         │
        │         ┌────────────┴────────────┐            │
        │         │                         │            │
        │ ┌───────────────────┐   ┌─────────────────────┐ │
        │ │ RandomLookupStrategy│ │ FirstAvailableStrategy│ │
        │ └───────────────────┘   └─────────────────────┘ │
        └─────────────────────────────────────────────────┘
```

---

## 💰 PAYMENT & PRICING SYSTEM

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  COST COMPUTATION│────▶│ PRICING STRATEGY│◄────│    PAYMENT      │
│─────────────────│     │─────────────────│     │─────────────────│
│-pricingStrategy │     │+calculate(ticket)│     │+pay(amount)     │
│                 │     │                 │     │                 │
│+compute(ticket) │     └─────────────────┘     └─────────────────┘
└─────────────────┘              ▲                        ▲
                                 │                        │
                    ┌────────────┴────────────┐          │
                    │                         │          │
           ┌─────────────────┐     ┌─────────────────┐   │
           │FixedPricingStrategy│   │HourlyPricingStrategy│  │
           └─────────────────┘     └─────────────────┘   │
                                                         │
                                            ┌────────────┴────────────┐
                                            │                         │
                                   ┌─────────────────┐     ┌─────────────────┐
                                   │  CASH PAYMENT   │     │  UPI PAYMENT    │
                                   └─────────────────┘     └─────────────────┘
```

---

## 🧠 MEMORY-FRIENDLY INTERVIEW NOTES

### **🔑 KEY DESIGN PATTERNS USED:**

1. **STRATEGY PATTERN** 🎯
   - **Where**: ParkingSpotLookupStrategy, PricingStrategy
   - **Why**: Different algorithms for spot selection and pricing
   - **Remember**: "Behavior at runtime"

2. **FACTORY PATTERN** 🏭
   - **Where**: SpotManagers (TwoWheeler, FourWheeler)
   - **Why**: Create different manager types based on vehicle
   - **Remember**: "Object creation delegation"

3. **COMPOSITION** 🧩
   - **Where**: ParkingLot → Building → Level → Manager → Spots
   - **Why**: "Has-a" relationships, loose coupling
   - **Remember**: "Building blocks approach"

### **⚡ CONCURRENCY HANDLING:**
- **ReentrantLock** in ParkingSpotManager
- **Thread-safe** spot allocation/deallocation
- **Remember**: "Lock before park/unpark"

### **📋 CORE ENTITIES (Remember as VTSL-P):**
- **V**ehicle (number, type)
- **T**icket (vehicle + spot + level + time)
- **S**pot (id, isFree, occupy/release)
- **L**evel (levelNumber, spotManagers)
- **P**arkingBuilding (levels, allocate/release)

---

## 🎤 INTERVIEW TALKING POINTS

### **1. SCALABILITY** 📈
- "Multiple levels can be added easily"
- "Different spot managers for different vehicle types"
- "Strategy pattern allows new lookup algorithms"

### **2. EXTENSIBILITY** 🔧
- "New vehicle types? Add new SpotManager"
- "New pricing models? Implement PricingStrategy"
- "New payment methods? Implement Payment interface"

### **3. THREAD SAFETY** 🔒
- "ReentrantLock ensures atomic operations"
- "Spot allocation is thread-safe"
- "Multiple vehicles can park simultaneously"

### **4. SINGLE RESPONSIBILITY** ✅
- Each class has one job:
  - **Gate**: Entry/Exit logic
  - **Manager**: Spot management
  - **Strategy**: Algorithm selection
  - **Building**: Overall coordination

### **5. KEY METHODS TO REMEMBER** 🧠
```java
// Entry Flow
vehicle → entranceGate.enter() → building.allocate() → level.park() → manager.park()

// Exit Flow  
ticket → exitGate.completeExit() → costComputation.compute() → payment.pay() → building.release()
```

---

## 🚀 QUICK REVISION CHECKLIST

✅ **Entities**: Vehicle, Ticket, Spot, Level, Building  
✅ **Enums**: VehicleType (TWO_WHEELER, FOUR_WHEELER)  
✅ **Patterns**: Strategy (Lookup + Pricing), Factory (SpotManagers)  
✅ **Concurrency**: ReentrantLock in managers  
✅ **Flow**: Enter → Allocate → Generate Ticket → Exit → Calculate → Pay → Release  
✅ **Extensibility**: New vehicle types, pricing models, payment methods  

---

## 💡 COMMON INTERVIEW EXTENSIONS

1. **"How to add BIKE support?"**
   → Create BikeSpotManager, add BIKE to VehicleType enum

2. **"How to implement time-based pricing?"**
   → Create HourlyPricingStrategy implementing PricingStrategy

3. **"What if spot sizes differ?"**
   → Add SpotSize enum, modify ParkingSpot class

4. **"How to handle reservations?"**
   → Add reservation system with time expiry

5. **"Database integration?"**
   → Add Repository pattern for persistence

**🎯 Remember: Always mention SOLID principles, design patterns, and scalability!**