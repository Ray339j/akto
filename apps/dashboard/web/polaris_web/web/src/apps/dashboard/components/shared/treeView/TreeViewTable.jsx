import React, { useEffect, useState } from 'react'
import GithubSimpleTable from '../../tables/GithubSimpleTable'
import dummyDataObj from './dummyData'
import func from '@/util/func'
import { IndexFiltersMode } from '@shopify/polaris'
import useTable from '../../tables/TableContext'
import treeViewFunc from './transform'

function TreeViewTable() {

    const {dummyData, sortOptions, resourceName, headers, promotedBulkActions} = dummyDataObj

    const [selectedTab, setSelectedTab] = useState("custom")
    const [selected, setSelected] = useState(1)
    function disambiguateLabel(key, value) {
        return func.convertToDisambiguateLabelObj(value, null, 2)
    }

    const definedTableTabs = ['Hostname', 'Custom']
    const { selectItems, selectedItems } = useTable();
    const [data, setData] = useState({'custom': [] ,'hostname': []})

    const { tabsInfo } = useTable()
    const tableCountObj = func.getTabsCount(definedTableTabs, data)
    const tableTabs = func.getTableTabsContent(definedTableTabs, tableCountObj, setSelectedTab, selectedTab, tabsInfo)
    const normalData = treeViewFunc.buildTree(dummyData, "displayName", ".", false, true, ":", headers)

    const prettifyAndRenderData = () => {
        const prettifyData = treeViewFunc.prettifyTreeViewData(normalData, headers, selectItems)
        setData({custom: prettifyData, hostname: []});
    }

    useEffect(() => {
        prettifyAndRenderData();
    },[])
    return (
        <GithubSimpleTable
            data={data[selectedTab]} 
            sortOptions={sortOptions} 
            resourceName={resourceName} 
            filters={[]}
            disambiguateLabel={disambiguateLabel} 
            headers={headers}
            selectable={true}
            promotedBulkActions={promotedBulkActions}
            mode={IndexFiltersMode.Default}
            headings={headers}
            useNewRow={true}
            condensedHeight={true}
            tableTabs={tableTabs}
            onSelect={(x) => setSelected(x)}
            selected={selected}
            csvFileName={"Inventory"}
            treeView={true}
        />
    )
}

export default TreeViewTable