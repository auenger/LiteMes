import { ref, watch, computed } from 'vue';

/**
 * Composable for table column settings (display/hidden, width).
 * Persists user preferences to localStorage.
 *
 * Usage:
 * const { visibleColumns, columnWidths, toggleColumn, setColumnWidth, resetSettings } = useTableSettings('company-list', columns);
 */

export interface ColumnDef {
  key: string;
  label: string;
  visible?: boolean;
  width?: number;
  defaultVisible?: boolean;
}

export function useTableSettings(tableId: string, defaultColumns: ColumnDef[]) {
  const STORAGE_KEY = `table-settings-${tableId}`;

  // Initialize defaults
  const columns = defaultColumns.map(col => ({
    ...col,
    defaultVisible: col.visible !== false,
    visible: col.visible !== false,
    width: col.width || undefined,
  }));

  // Load from localStorage
  const savedSettings = loadSettings(tableId);
  if (savedSettings) {
    for (const col of columns) {
      const saved = savedSettings[col.key];
      if (saved) {
        col.visible = saved.visible;
        if (saved.width) {
          col.width = saved.width;
        }
      }
    }
  }

  const columnsRef = ref(columns);

  const visibleColumns = computed(() =>
    columnsRef.value.filter(col => col.visible)
  );

  function toggleColumn(key: string) {
    const col = columnsRef.value.find(c => c.key === key);
    if (col) {
      col.visible = !col.visible;
      saveSettings();
    }
  }

  function setColumnWidth(key: string, width: number) {
    const col = columnsRef.value.find(c => c.key === key);
    if (col) {
      col.width = width;
      saveSettings();
    }
  }

  function resetSettings() {
    for (const col of columnsRef.value) {
      col.visible = col.defaultVisible !== false;
      col.width = undefined;
    }
    saveSettings();
  }

  function saveSettings() {
    const settings: Record<string, { visible: boolean; width?: number }> = {};
    for (const col of columnsRef.value) {
      settings[col.key] = {
        visible: col.visible,
        width: col.width,
      };
    }
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(settings));
    } catch {
      // Ignore storage errors
    }
  }

  return {
    columns: columnsRef,
    visibleColumns,
    toggleColumn,
    setColumnWidth,
    resetSettings,
  };
}

function loadSettings(tableId: string): Record<string, { visible: boolean; width?: number }> | null {
  try {
    const raw = localStorage.getItem(`table-settings-${tableId}`);
    if (raw) {
      return JSON.parse(raw);
    }
  } catch {
    // Ignore
  }
  return null;
}
